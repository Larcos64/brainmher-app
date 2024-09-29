package com.uan.brainmher.application.ui.fragments.memorizame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.memorizame.MemorizameFamilyGridAdapter;
import com.uan.brainmher.application.ui.helpers.NavigationHelper;
import com.uan.brainmher.databinding.FragmentNewCardMemorizameBinding;
import com.uan.brainmher.domain.entities.Carer;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.databinding.FragmentCuMemorizameFamilyBinding;
import com.uan.brainmher.domain.entities.Patient;
import com.uan.brainmher.domain.repositories.MemorizameRepository;
import com.uan.brainmher.infraestructure.tools.CircularProgressUtil;
import com.uan.brainmher.infraestructure.tools.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemorizameFamilyFragment extends Fragment {

    private FragmentCuMemorizameFamilyBinding binding;
    private FragmentNewCardMemorizameBinding bindingNewCard;
    private int flag;
    private Uri uriImage;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private MemorizameFamilyGridAdapter adapter;
    private MemorizameFamilyGridAdapter.ISelectionMemorizame iSelectionMemorizame;
    private CircularProgressUtil circularProgressUtil;
    private Patient patient;
    private String categoria = "";
    private StorageReference storageReference;
    public static final int REQUEST_CODE2 = 10;

    public MemorizameFamilyFragment(int flag) {
        this.flag = flag;
    }

    private ActivityResultLauncher<Intent> startActivityLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCuMemorizameFamilyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        final Bundle bundle = getArguments();
        if (bundle != null) {
            patient = (Patient) bundle.getSerializable("patient");
        }

        switch (flag) {
            case 1:
                categoria = "Family";
                binding.ivQuestion.setImageResource(R.drawable.img_family_question);
                binding.titleAddImage.setText("Agregar pregunta de familia");
                break;
            case 2:
                categoria = "Pets";
                binding.ivQuestion.setImageResource(R.drawable.img_pets_question);
                binding.titleAddImage.setText("Agregar pregunta de mascotas");
                break;
            case 3:
                categoria = "Home";
                binding.ivQuestion.setImageResource(R.drawable.img_home_question);
                binding.titleAddImage.setText("Agregar pregunta de hogar");
                break;
            case 4:
                categoria = "Places";
                binding.ivQuestion.setImageResource(R.drawable.img_places_question);
                binding.titleAddImage.setText("Agregar pregunta de lugar");
                break;
        }

        binding.cvAddImage.setOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            final FragmentTransaction transaction = manager.beginTransaction();
            Fragment change = new NewCardMemorizame(flag);
            change.setArguments(bundle);
            transaction.replace(R.id.container_memorizame_parent, change).addToBackStack(null).commit();
        });

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        circularProgressUtil = new CircularProgressUtil(getActivity());

        logicEventSelecItem();
        initRecyclerView();

        startActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uriImage = result.getData().getData();
                        Glide.with(requireContext())
                                .load(uriImage)
                                .fitCenter()
                                .into(bindingNewCard.civProfileImage);
                    }
                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Asegúrate de actualizar el adaptador cuando el fragmento vuelve a estar visible
        if (binding.recyclerView.getAdapter() != null) {
            binding.recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void logicEventSelecItem() {
        iSelectionMemorizame = new MemorizameFamilyGridAdapter.ISelectionMemorizame() {
            @Override
            public void clickItem(final Memorizame memorizame) {
                final AlertDialog alertDialog;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BackgroundRounded);

                try {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.fragment_new_card_memorizame, null);
                    builder.setView(dialogView);
                    alertDialog = builder.create();

                    CircleImageView imageUpdate = dialogView.findViewById(R.id.civ_profile_image);
                    TextInputEditText editQuestion = dialogView.findViewById(R.id.edit_question);
                    TextInputEditText editAnswer1 = dialogView.findViewById(R.id.edit_answer1);
                    TextInputEditText editAnswer2 = dialogView.findViewById(R.id.edit_answer2);
                    TextInputEditText editAnswer3 = dialogView.findViewById(R.id.edit_answer3);
                    TextInputEditText editAnswer4 = dialogView.findViewById(R.id.edit_answer4);
                    AutoCompleteTextView rtaAut = dialogView.findViewById(R.id.edit_correct_answer);
                    MaterialButton btnActualizar = dialogView.findViewById(R.id.button_create_memorizame);

                    // Configura el adaptador de datos para el AutoCompleteTextView
                    String[] correctAnswerArray = {"1", "2", "3", "4"};
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                            R.layout.dropdown_menu_popup_item, correctAnswerArray);
                    // Aplica el adaptador de datos
                    rtaAut.setAdapter(arrayAdapter);

                    editQuestion.setText(memorizame.getQuestion());
                    editAnswer1.setText(memorizame.getAnswer1());
                    editAnswer2.setText(memorizame.getAnswer2());
                    editAnswer3.setText(memorizame.getAnswer3());
                    editAnswer4.setText(memorizame.getAnswer4());

                    Glide.with(getActivity()).load(memorizame.getUriImg()).fitCenter().into(imageUpdate);

                    // Asignación del evento al botón de actualización
                    btnActualizar.setOnClickListener(view -> {
                        circularProgressUtil.showProgress(getString(R.string.updating));

                        // Recolecta los datos actualizados
                        String question = editQuestion.getText().toString();
                        String answer1 = editAnswer1.getText().toString();
                        String answer2 = editAnswer2.getText().toString();
                        String answer3 = editAnswer3.getText().toString();
                        String answer4 = editAnswer4.getText().toString();
                        String rtaCorrect = rtaAut.getText().toString();

                        if (!question.isEmpty() && !answer1.isEmpty() && !answer2.isEmpty()
                                && !answer3.isEmpty() && !answer4.isEmpty() && !rtaCorrect.isEmpty()) {
                            // Actualiza los campos en el objeto Memorizame
                            memorizame.setQuestion(question);
                            memorizame.setAnswer1(answer1);
                            memorizame.setAnswer2(answer2);
                            memorizame.setAnswer3(answer3);
                            memorizame.setAnswer4(answer4);

                            // Establece la respuesta correcta
                            switch (rtaCorrect) {
                                case "1":
                                    memorizame.setCorrectAnswer(answer1);
                                    break;
                                case "2":
                                    memorizame.setCorrectAnswer(answer2);
                                    break;
                                case "3":
                                    memorizame.setCorrectAnswer(answer3);
                                    break;
                                case "4":
                                    memorizame.setCorrectAnswer(answer4);
                                    break;
                            }

                            // Usa el repository para guardar la actualización
                            MemorizameRepository memorizameRepository = new MemorizameRepository();
                            memorizameRepository.createMemorizame(memorizame, categoria, uriImage, new MemorizameRepository.OnMemorizameCreatedListener() {
                                @Override
                                public void onSuccess(Memorizame updatedMemorizame) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.was_saved_succesfully), Toast.LENGTH_SHORT).show();
                                    circularProgressUtil.hideProgress();
                                    alertDialog.dismiss(); // Cierra el dialogo al completar la actualización
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.elimination_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    circularProgressUtil.hideProgress();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialog.show();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void clickdelete(final Memorizame memorizame) {
                // Lógica para eliminar
            }
        };
    }

    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.recyclerView.setItemAnimator(null);

        Query query = db.collection(Constants.Memorizame)
                .document(patient.getPatientUID()).collection(categoria);

        FirestoreRecyclerOptions<Memorizame> options =
                new FirestoreRecyclerOptions.Builder<Memorizame>()
                        .setQuery(query, Memorizame.class)
                        .setLifecycleOwner(this) // Añadir el lifecycle owner
                        .build();

        adapter = new MemorizameFamilyGridAdapter(options, getActivity(), iSelectionMemorizame);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Manejo de retroceso y navegación al fragmento anterior
                NavigationHelper.navigateToFragment(requireActivity().getSupportFragmentManager(), MemorizameFamilyFragment.class, R.id.container_memorizame_parent);
            }
        });
    }
}