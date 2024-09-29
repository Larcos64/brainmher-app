package com.uan.brainmher.application.ui.helpers;

import android.content.Context;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.uan.brainmher.R;
import com.uan.brainmher.databinding.FragmentNewCardMemorizameBinding;
import com.uan.brainmher.domain.entities.Memorizame;
import com.uan.brainmher.domain.entities.Patient;

public class MemorizameFormHelper {

    private final Context context;
    private final Memorizame memorizame;
    private final FragmentNewCardMemorizameBinding binding;

    public MemorizameFormHelper(Context context, Memorizame memorizame, FragmentNewCardMemorizameBinding binding) {
        this.context = context;
        this.memorizame = memorizame;
        this.binding = binding;
    }

    public void setupDropdownMenu() {
        String[] correctAnswerArray = {"1", "2", "3", "4"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                R.layout.dropdown_menu_popup_item, correctAnswerArray);
        binding.editCorrectAnswer.setAdapter(arrayAdapter);
    }

    public void setCorrectAnswer(String correct) {
        switch (correct) {
            case "1":
                memorizame.setCorrectAnswer(binding.editAnswer1.getText().toString());
                break;
            case "2":
                memorizame.setCorrectAnswer(binding.editAnswer2.getText().toString());
                break;
            case "3":
                memorizame.setCorrectAnswer(binding.editAnswer3.getText().toString());
                break;
            case "4":
                memorizame.setCorrectAnswer(binding.editAnswer4.getText().toString());
                break;
        }
    }

    public boolean isFormValid(Uri uriImage) {
        boolean areFieldsValid = !binding.editQuestion.getText().toString().isEmpty()
                && !binding.editAnswer1.getText().toString().isEmpty()
                && !binding.editAnswer2.getText().toString().isEmpty()
                && !binding.editAnswer3.getText().toString().isEmpty()
                && !binding.editAnswer4.getText().toString().isEmpty()
                && binding.editCorrectAnswer.getText().toString() != null;

        boolean isImageSelected = uriImage != null;

        if (!areFieldsValid) {
            Toast.makeText(context, context.getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isImageSelected) {
            Toast.makeText(context, context.getString(R.string.select_photo), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void fillFormWithData(Memorizame memorizame) {
        binding.editQuestion.setText(memorizame.getQuestion());
        binding.editAnswer1.setText(memorizame.getAnswer1());
        binding.editAnswer2.setText(memorizame.getAnswer2());
        binding.editAnswer3.setText(memorizame.getAnswer3());
        binding.editAnswer4.setText(memorizame.getAnswer4());

        // Obtener la respuesta correcta y compararla con las respuestas del formulario
        String correctRTA = memorizame.getCorrectAnswer();

        if (correctRTA.equals(memorizame.getAnswer1())) {
            binding.editCorrectAnswer.setText("1");
        } else if (correctRTA.equals(memorizame.getAnswer2())) {
            binding.editCorrectAnswer.setText("2");
        } else if (correctRTA.equals(memorizame.getAnswer3())) {
            binding.editCorrectAnswer.setText("3");
        } else if (correctRTA.equals(memorizame.getAnswer4())) {
            binding.editCorrectAnswer.setText("4");
        }
    }

    public boolean setPojoMemorizame(Uri uriImage, Patient patient) {
        // Obtener valores del formulario
        String question = binding.editQuestion.getText().toString();
        String answer1 = binding.editAnswer1.getText().toString();
        String answer2 = binding.editAnswer2.getText().toString();
        String answer3 = binding.editAnswer3.getText().toString();
        String answer4 = binding.editAnswer4.getText().toString();
        String correct = binding.editCorrectAnswer.getText().toString();

        // Verificar si los campos son válidos
        if (isFormValid(uriImage)) {
            // Setear la información en el objeto Memorizame
            memorizame.setQuestion(question);
            memorizame.setAnswer1(answer1);
            memorizame.setAnswer2(answer2);
            memorizame.setAnswer3(answer3);
            memorizame.setAnswer4(answer4);
            memorizame.setPatientUID(patient.getPatientUID());  // Asegurarse de que el UID esté presente
            setCorrectAnswer(correct);  // Asignar la respuesta correcta
            return true;
        } else {
            Toast.makeText(context, uriImage == null ? context.getString(R.string.select_photo) : context.getString(R.string.complete_field), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
