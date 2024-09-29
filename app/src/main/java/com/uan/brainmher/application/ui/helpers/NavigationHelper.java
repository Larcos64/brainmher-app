package com.uan.brainmher.application.ui.helpers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class NavigationHelper {

    /**
     * Navega al fragmento anterior en la pila de fragmentos, si no hay fragmentos,
     * instancia y navega al fragmento proporcionado como clase de fallback.
     *
     * @param fragmentManager El FragmentManager utilizado para la transacción de fragmentos.
     * @param fallbackFragmentClass La clase del fragmento a navegar si no hay fragmentos en la pila.
     * @param containerId El ID del layout donde se reemplazará el fragmento.
     */
    public static void navigateToFragment(FragmentManager fragmentManager, Class<? extends Fragment> fallbackFragmentClass, int containerId) {
        // Verificar si hay fragmentos en la pila
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();  // Retrocede al fragmento anterior
        } else {
            // Si no hay fragmentos en la pila, instanciar el fragmento de fallback
            try {
                Fragment fallbackFragment = fallbackFragmentClass.newInstance();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(containerId, fallbackFragment)
                        .addToBackStack(null)  // Añadir a la pila si es necesario
                        .commit();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                // Manejar la excepción adecuadamente
            }
        }
    }
}