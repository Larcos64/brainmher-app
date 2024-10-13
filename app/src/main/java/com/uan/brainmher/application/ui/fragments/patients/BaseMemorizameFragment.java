package com.uan.brainmher.application.ui.fragments.patients;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.uan.brainmher.R;
import com.uan.brainmher.application.ui.adapters.patient.PatientMemorizameAdapter;
import com.uan.brainmher.databinding.FragmentMemorizameChildBinding;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseMemorizameFragment extends Fragment {

    private FragmentMemorizameChildBinding binding;
    private PatientMemorizameAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private List<Memorizame> memorizameList = new ArrayList<>();
    private String seleccionRG = "";
    private PatientMemorizameAdapter.ISelectionMemorizame iSelectionMemorizame;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMemorizameChildBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        initRecycler();
        setupUI();
        setupItemSelectLogic();
    }

    private void setupItemSelectLogic() {
        iSelectionMemorizame = memorizame -> onItemSelected(memorizame);
    }

    private void initRecycler() {
        binding.recyclerViewMemorizame.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        CollectionReference collectionReference = db.collection(Constants.Memorizame);

        collectionReference.document(user.getUid()).collection(getCollectionName()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    memorizameList.clear();
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Memorizame memorizame = snapshot.toObject(Memorizame.class);
                        memorizameList.add(memorizame);
                    }
                    adapter = new PatientMemorizameAdapter(memorizameList, requireActivity(), iSelectionMemorizame);
                    binding.recyclerViewMemorizame.setAdapter(adapter);
                    binding.layoutDescriptionMemorizame.setVisibility(
                            memorizameList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
                });
    }

    private void setupUI() {
        binding.lblMemogameName.setText(getTabTitle());
        binding.lblMemogameDesc.setText(getTabDescription());
        Glide.with(this).load(getTabImage()).into(binding.ivMemogame);
    }

    protected abstract String getCollectionName();
    protected abstract String getTabTitle();
    protected abstract String getTabDescription();
    protected abstract int getTabImage();

    private void onItemSelected(Memorizame memorizame) {
        // Lógica al seleccionar un ítem
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.BackgroundRounded);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_question_memorizame, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        // Configuración del contenido del AlertDialog
        dialogView.<RadioGroup>findViewById(R.id.rg_question).clearCheck();
        dialogView.<RadioButton>findViewById(R.id.rb_question_1).setText(memorizame.getAnswer1());
        dialogView.<RadioButton>findViewById(R.id.rb_question_2).setText(memorizame.getAnswer2());
        dialogView.<RadioButton>findViewById(R.id.rb_question_3).setText(memorizame.getAnswer3());
        dialogView.<RadioButton>findViewById(R.id.rb_question_4).setText(memorizame.getAnswer4());

        ImageView imgQuestion = dialogView.findViewById(R.id.img_question);
        Glide.with(requireActivity())
                .load(memorizame.getUriImg())
                .fitCenter()
                .into(imgQuestion);

        dialogView.findViewById(R.id.btn_cancelar).setOnClickListener(v -> alertDialog.dismiss());
        dialogView.findViewById(R.id.btn_terminar).setOnClickListener(v -> {
            RadioGroup rg = dialogView.findViewById(R.id.rg_question);
            int selectedId = rg.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadio = rg.findViewById(selectedId);
                seleccionRG = selectedRadio.getText().toString();
                if (seleccionRG.equals(memorizame.getCorrectAnswer())) {
                    showAlert(getString(R.string.msg_memorizame_success), memorizame);
                } else {
                    showAlert(getString(R.string.msg_memorizame_failed), memorizame);
                }
                alertDialog.dismiss();
            } else {
                Toast.makeText(requireActivity(), R.string.select_answer, Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.show();
    }

    private void showAlert(String message, final Memorizame memorizame) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.BackgroundRounded);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_one_textview_two_buttons, null);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        dialogView.findViewById(R.id.btn1).setOnClickListener(v -> alertDialog.dismiss());
        dialogView.findViewById(R.id.btn2).setOnClickListener(v -> {
            onItemSelected(memorizame);
            alertDialog.dismiss();
        });

        dialogView.<TextView>findViewById(R.id.textView).setText(message);
        alertDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}