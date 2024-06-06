package org.example.Servicio;
import org.example.Modelo.Contacto;
import org.example.Modelo.NodoContacto;
import org.example.Modelo.Contacto;
import org.example.Modelo.NodoContacto;
import javax.swing.*;
import java.io.*;
import java.time.LocalDate;

// La clase Agenda implementa Serializable para permitir la serialización del árbol de contactos.
public class Agenda implements Serializable {
    private NodoContacto raiz;

    // Constructor de la clase Agenda
    public Agenda() {
        this.raiz = null;
    }

    // Método para agregar un contacto al árbol
    public void agregarContacto(String nombre, long telefono, String correoElectronico, LocalDate fechaNacimiento) {
        Contacto nuevoContacto = new Contacto(nombre, telefono, correoElectronico, fechaNacimiento);
        if (this.raiz == null) {
            this.raiz = new NodoContacto(nuevoContacto);
        } else {
            this.insertar(this.raiz, nuevoContacto);
        }
    }

    // Método privado recursivo para insertar un contacto en el árbol
    private void insertar(NodoContacto padre, Contacto contacto) {
        if (contacto.getNombre().compareTo(padre.getContacto().getNombre()) < 0) {
            if (padre.getIzdo() == null) {
                padre.setIzdo(new NodoContacto(contacto));
            } else {
                insertar(padre.getIzdo(), contacto);
            }
        } else if (contacto.getNombre().compareTo(padre.getContacto().getNombre()) > 0) {
            if (padre.getDcho() == null) {
                padre.setDcho(new NodoContacto(contacto));
            } else {
                insertar(padre.getDcho(), contacto);
            }
        }
    }

    // Método para buscar un contacto basado en un criterio (nombre, teléfono o correo)
    public Contacto buscarContacto(String criterio, String valor) {
        switch (criterio.toLowerCase()) {
            case "nombre":
                return buscarPorNombre(this.raiz, valor);
            case "telefono":
                return buscarPorTelefono(this.raiz, Long.parseLong(valor));
            case "correo":
                return buscarPorCorreo(this.raiz, valor);
            default:
                return null;
        }
    }

    // Método privado recursivo para buscar un contacto por nombre
    private Contacto buscarPorNombre(NodoContacto nodo, String nombre) {
        if (nodo == null) {
            return null;
        }
        if (nombre.equals(nodo.getContacto().getNombre())) {
            return nodo.getContacto();
        } else if (nombre.compareTo(nodo.getContacto().getNombre()) < 0) {
            return buscarPorNombre(nodo.getIzdo(), nombre);
        } else {
            return buscarPorNombre(nodo.getDcho(), nombre);
        }
    }

    // Método privado recursivo para buscar un contacto por teléfono
    private Contacto buscarPorTelefono(NodoContacto nodo, long telefono) {
        if (nodo == null) {
            return null;
        }
        if (telefono == nodo.getContacto().getTelefono()) {
            return nodo.getContacto();
        }
        Contacto izdo = buscarPorTelefono(nodo.getIzdo(), telefono);
        if (izdo != null) {
            return izdo;
        } else {
            return buscarPorTelefono(nodo.getDcho(), telefono);
        }
    }

    // Método privado recursivo para buscar un contacto por correo
    private Contacto buscarPorCorreo(NodoContacto nodo, String correo) {
        if (nodo == null) {
            return null;
        }
        if (correo.equals(nodo.getContacto().getCorreoElectronico())) {
            return nodo.getContacto();
        } else {
            Contacto izdo = buscarPorCorreo(nodo.getIzdo(), correo);
            if (izdo != null) {
                return izdo;
            } else {
                return buscarPorCorreo(nodo.getDcho(), correo);
            }
        }
    }

    // Nuevo método para buscar un contacto utilizando un objeto Contacto como parámetro
    public Contacto buscar(Contacto contacto) {
        return buscarEnNodo(this.raiz, contacto);
    }

    // Método privado recursivo para buscar un contacto en el árbol usando un objeto Contacto
    private Contacto buscarEnNodo(NodoContacto nodo, Contacto contacto) {
        if (nodo == null) {
            return null;
        }

        boolean coincide = true;
        if (contacto.getNombre() != null && !contacto.getNombre().isEmpty()) {
            coincide = coincide && contacto.getNombre().equals(nodo.getContacto().getNombre());
        }
        if (contacto.getTelefono() != 0) {
            coincide = coincide && contacto.getTelefono() == nodo.getContacto().getTelefono();
        }
        if (contacto.getCorreoElectronico() != null && !contacto.getCorreoElectronico().isEmpty()) {
            coincide = coincide && contacto.getCorreoElectronico().equals(nodo.getContacto().getCorreoElectronico());
        }

        if (coincide) {
            return nodo.getContacto();
        }

        Contacto izdo = buscarEnNodo(nodo.getIzdo(), contacto);
        if (izdo != null) {
            return izdo;
        }

        return buscarEnNodo(nodo.getDcho(), contacto);
    }

    // Método para serializar (guardar) la agenda en un archivo
    public void guardarAgenda(String archivo) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            // Se escribe el objeto raíz (la agenda) en el archivo
            oos.writeObject(this.raiz);
        }
    }

    // Método para deserializar (cargar) la agenda desde un archivo
    public void cargarAgenda(String archivo) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            // Se lee el objeto raíz (la agenda) desde el archivo
            this.raiz = (NodoContacto) ois.readObject();
        }
    }

    // Método para eliminar un contacto del árbol
    public void eliminarContacto(String nombre) {
        this.raiz = eliminar(this.raiz, nombre);
    }

    // Método privado recursivo para eliminar un contacto del árbol
    private NodoContacto eliminar(NodoContacto nodo, String nombre) {
        if (nodo == null) {
            return null;
        }
        if (nombre.compareTo(nodo.getContacto().getNombre()) < 0) {
            nodo.setIzdo(eliminar(nodo.getIzdo(), nombre));
        } else if (nombre.compareTo(nodo.getContacto().getNombre()) > 0) {
            nodo.setDcho(eliminar(nodo.getDcho(), nombre));
        } else {
            // Nodo con un solo hijo o sin hijos
            if (nodo.getIzdo() == null) {
                return nodo.getDcho();
            } else if (nodo.getDcho() == null) {
                return nodo.getIzdo();
            }

            // Nodo con dos hijos: obtener el sucesor en inorden (el menor en el subárbol derecho)
            NodoContacto temp = minValorNodo(nodo.getDcho());
            // Copiar el contenido del sucesor en inorden a este nodo
            nodo.getContacto().setTelefono(temp.getContacto().getTelefono());
            nodo.getContacto().setNombre(temp.getContacto().getNombre());
            nodo.getContacto().setCorreoElectronico(temp.getContacto().getCorreoElectronico());
            nodo.getContacto().setFechaNacimiento(temp.getContacto().getFechaNacimiento());
            // Eliminar el sucesor en inorden
            nodo.setDcho(eliminar(nodo.getDcho(), temp.getContacto().getNombre()));
        }
        return nodo;
    }

    // Método privado para encontrar el nodo con el valor mínimo en el subárbol derecho
    private NodoContacto minValorNodo(NodoContacto nodo) {
        NodoContacto actual = nodo;
        // Recorrer hasta encontrar la hoja más a la izquierda
        while (actual.getIzdo() != null) {
            actual = actual.getIzdo();
        }
        return actual;
    }

    // Método para mostrar todos los contactos en un JTextArea
    public void mostrarContactos(JTextArea textArea) {
        textArea.setText("");
        inOrden(this.raiz, textArea);
    }

    // Método privado recursivo para realizar un recorrido en orden del árbol y mostrar los contactos
    private void inOrden(NodoContacto nodo, JTextArea textArea) {
        if (nodo != null) {
            inOrden(nodo.getIzdo(), textArea);
            textArea.append("Nombre: " + nodo.getContacto().getNombre() + ", Teléfono: " + nodo.getContacto().getTelefono() +
                    ", Correo: " + nodo.getContacto().getCorreoElectronico() + ", Fecha de Nacimiento: " + nodo.getContacto().getFechaNacimiento() + "\n");
            inOrden(nodo.getDcho(), textArea);
        }
    }
}
