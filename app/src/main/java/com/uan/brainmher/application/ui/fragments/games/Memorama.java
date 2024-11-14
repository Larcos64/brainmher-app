package com.uan.brainmher.application.ui.fragments.games;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.uan.brainmher.R;
import com.uan.brainmher.domain.entities.MemoramaEntity;
import com.uan.brainmher.domain.entities.ScoreGame;
import com.uan.brainmher.infraestructure.tools.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Memorama extends Fragment {

    //region Reference ImageViews
    private ImageView itemUno;
    private ImageView itemDos;
    private ImageView itemTres;
    private ImageView itemCuatro;
    private ImageView itemCinco;
    private ImageView itemSeis;
    private ImageView itemSiete;
    private ImageView itemOcho;
    private ImageView itemNueve;
    private ImageView itemDiez;
    private ImageView itemOnce;
    private ImageView itemDoce;
    private ImageView itemTrece;
    private ImageView itemCatorce;
    private ImageView itemQuince;
    private ImageView itemDieciseis;
    //endregion

    private List<ImageView> imageViews;
    private List<MemoramaEntity> listaComplete;
    private MemoramaEntity elemetSave;
    private boolean clickAllElements = true;
    private Memoramai memoramai;
    private long tInicio;
    private double calificacion = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    public Memorama(Memoramai memoramai) {
        this.memoramai = memoramai;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memorama, container, false);

        // Bind views
        itemUno = view.findViewById(R.id.item_uno);
        itemDos = view.findViewById(R.id.item_dos);
        itemTres = view.findViewById(R.id.item_tres);
        itemCuatro = view.findViewById(R.id.item_cuatro);
        itemCinco = view.findViewById(R.id.item_cinco);
        itemSeis = view.findViewById(R.id.item_seis);
        itemSiete = view.findViewById(R.id.item_siete);
        itemOcho = view.findViewById(R.id.item_ocho);
        itemNueve = view.findViewById(R.id.item_nueve);
        itemDiez = view.findViewById(R.id.item_diez);
        itemOnce = view.findViewById(R.id.item_once);
        itemDoce = view.findViewById(R.id.item_doce);
        itemTrece = view.findViewById(R.id.item_trece);
        itemCatorce = view.findViewById(R.id.item_catorce);
        itemQuince = view.findViewById(R.id.item_quince);
        itemDieciseis = view.findViewById(R.id.item_dieciseis);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initGame();
    }

    private void initGame() {
        listaComplete = getDataListComplete();
        imageViews = new ArrayList<>();
        imageViews.add(itemUno);
        imageViews.add(itemDos);
        imageViews.add(itemTres);
        imageViews.add(itemCuatro);
        imageViews.add(itemCinco);
        imageViews.add(itemSeis);
        imageViews.add(itemSiete);
        imageViews.add(itemOcho);
        imageViews.add(itemNueve);
        imageViews.add(itemDiez);
        imageViews.add(itemOnce);
        imageViews.add(itemDoce);
        imageViews.add(itemTrece);
        imageViews.add(itemCatorce);
        imageViews.add(itemQuince);
        imageViews.add(itemDieciseis);

        new Thread(() -> {
            try {
                runOnUIThread(true);
                Thread.sleep(5000);
                runOnUIThread(false);
                tInicio = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runOnUIThread(final boolean showAllImages) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                int position = 0;
                for (ImageView item : imageViews) {
                    if (showAllImages) {
                        item.setImageResource(listaComplete.get(position).getImageId());
                        item.setOnClickListener(null);
                    } else {
                        item.setImageResource(R.drawable.ic_logo_carta);
                        final int finalPosition = position;
                        item.setOnClickListener(v -> clickListener(finalPosition));
                    }
                    position++;
                }
            });
        }
    }

    private void clickListener(final int finalPosition) {
        if (clickAllElements) {
            MemoramaEntity lista = listaComplete.get(finalPosition);

            if (lista.isClick() && !lista.isFinded()) {
                if (elemetSave == null) {
                    elemetSave = lista;
                    imageViews.get(finalPosition).setImageResource(lista.getImageId());
                } else {
                    if (elemetSave != lista) {
                        MemoramaEntity listaSave = listaComplete.get(elemetSave.getPosition());
                        imageViews.get(finalPosition).setImageResource(lista.getImageId());

                        if (lista.getImgGroup() == elemetSave.getImgGroup()) {
                            calificacion += 12.5;
                            lista.setFinded(true);
                            listaSave.setFinded(true);
                            lista.setClick(false);
                            listaSave.setClick(false);
                            elemetSave = null;

                            if (isGameCompleted()) {
                                alertWin(calificacion);
                            }
                        } else {
                            calificacion -= 6.25;
                            Toast.makeText(getContext(), "Sigue intentando", Toast.LENGTH_SHORT).show();
                            lista.setClick(false);
                            listaSave.setClick(false);
                            clickAllElements = false;

                            new Handler().postDelayed(() -> {
                                imageViews.get(elemetSave.getPosition()).setImageResource(R.drawable.ic_logo_carta);
                                imageViews.get(finalPosition).setImageResource(R.drawable.ic_logo_carta);
                                lista.setClick(true);
                                listaSave.setClick(true);
                                clickAllElements = true;
                                elemetSave = null;
                            }, 750);
                        }
                    }
                }
            }
        }
    }

    private boolean isGameCompleted() {
        for (MemoramaEntity item : listaComplete) {
            if (!item.isFinded()) {
                return false;
            }
        }
        return true;
    }

    private List<MemoramaEntity> getDataListComplete() {
        List<MemoramaEntity> memoramaEntities = getDataList();
        List<Integer> numbersUsed = new ArrayList<>();

        for (MemoramaEntity item : new ArrayList<>(memoramaEntities)) {
            boolean notFoundPosition = true;
            while (notFoundPosition) {
                int aleatorio = numberAleatory();
                if (!numbersUsed.contains(aleatorio)) {
                    numbersUsed.add(aleatorio);
                    item.setPosition(aleatorio);
                    memoramaEntities.set(aleatorio, item);
                    notFoundPosition = false;
                }
            }
        }
        return memoramaEntities;
    }

    private List<MemoramaEntity> getDataList() {
        List<MemoramaEntity> memoramaEntities = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_uno_icon, 1));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_dos_icon, 2));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_tres_icon, 3));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_cuatro_icon, 4));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_cinco_icon, 5));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_seis_icon, 6));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_siete_icon, 7));
            memoramaEntities.add(new MemoramaEntity(R.drawable.boy_ocho_icon, 8));
        }
        return memoramaEntities;
    }

    private int numberAleatory() {
        //funcion que retorna numero aleatorio
        final int min = 0;
        final int max = 15;
        return new Random().nextInt((max - min) + 1) + min;
    }

    private void alertWin(Double puntuacion) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View layoutInflater = getLayoutInflater().inflate(R.layout.memorama_win_template, null);
        builder.setView(layoutInflater);
        Button btnOnback = layoutInflater.findViewById(R.id.mwmorama_winp_btnonback);
        Button btnReload = layoutInflater.findViewById(R.id.mwmorama_winp_reload);
        TextView txtPuntuacion = layoutInflater.findViewById(R.id.txt_puntuacion);

        AlertDialog dialog = builder.create();
        dialog.show();

        final AlertDialog finalDialog = dialog;

        btnOnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
                memoramai.callOnbackPressed();
            }
        });

        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.dismiss();
                memoramai.reloadGame("Memorama");
            }
        });
        long fJuego = System.currentTimeMillis();
        long diferencia = (fJuego - tInicio) / 1000;
        if (puntuacion <= 0) {
            puntuacion = 0d;
        } else {

            double scorePorcentaje = 0.7;
            double timePorcentaje = 0.3;
            double scoreTime = 100 * (23 / diferencia);

            if (scoreTime >= 100) {
                scoreTime = 100d;
                puntuacion = (scoreTime * timePorcentaje) + (puntuacion * scorePorcentaje);
            } else {
                puntuacion = (scoreTime * timePorcentaje) + (puntuacion * scorePorcentaje);
            }
        }

        DocumentReference docRefAd = db.collection(Constants.ScoreGames).document(firebaseUser.getUid());
        final double finalPuntuacion = puntuacion;
        docRefAd.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ScoreGame scoreGame = documentSnapshot.toObject(ScoreGame.class);

                if(scoreGame != null){
                    if (scoreGame.getGameMemoramaScore() == null) {
                        //esta vacia crea una nueva lista.
                        List<Integer> list = new ArrayList<>();
                        list.add((int)finalPuntuacion);
                        //agrega puntiacion
                        scoreGame.setGameMemoramaScore(list);
                    } else {
                        //ya existe lista, obtiene la actual y agrega nuevo
                        List<Integer> list = scoreGame.getGameMemoramaScore();
                        list.add((int)finalPuntuacion);
                        scoreGame.setGameMemoramaScore(list);
                    }
                }else{
                    scoreGame = new ScoreGame();

                    //esta vacia crea una nueva lista.
                    List<Integer> list = new ArrayList<>();
                    list.add((int)finalPuntuacion);
                    //agrega puntiacion
                    scoreGame.setGameMemoramaScore(list);
                }

                //guardar nuevos datos
                db.collection(Constants.ScoreGames).document(firebaseAuth.getUid()).set(scoreGame);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error. " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        txtPuntuacion.setText(String.valueOf(finalPuntuacion));
    }

    public interface Memoramai {
        void reloadGame(String reloadGame);

        void callOnbackPressed();
    }
}
