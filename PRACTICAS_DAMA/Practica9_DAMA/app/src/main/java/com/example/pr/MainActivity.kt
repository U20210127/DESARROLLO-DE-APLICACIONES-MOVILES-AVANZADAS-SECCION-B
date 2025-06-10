package com.example.pr

import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: ContactAdapter
    private var contactList = mutableListOf<Contact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializamos el adaptador con las funciones de editar y eliminar
        adapter = ContactAdapter(contactList,
            onEdit = { contact -> showAddEditDialog(contact) },
            onDelete = { contact -> showDeleteConfirmationDialog(contact) }
        )

        // Configuración del RecyclerView
        findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(this)

        // Configuración del FloatingActionButton para agregar un nuevo contacto
        findViewById<FloatingActionButton>(R.id.btnAdd).setOnClickListener {
            showAddEditDialog(null)
        }

        // Cargar la lista de contactos desde la base de datos
        fetchContacts()
    }

    // Método para obtener los contactos desde el servidor
    private fun fetchContacts() {
        RetrofitClient.instance.getContacts().enqueue(object : Callback<List<Contact>> {
            override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
                if (response.isSuccessful) {
                    contactList = response.body()?.toMutableList() ?: mutableListOf()
                    adapter.updateList(contactList)
                } else {
                    Toast.makeText(this@MainActivity, "Error al cargar los contactos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Mostrar el diálogo para agregar o editar un contacto
    private fun showAddEditDialog(contact: Contact?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit, null)
        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtPhone = dialogView.findViewById<EditText>(R.id.edtPhone)
        val edtEmail = dialogView.findViewById<EditText>(R.id.edtEmail)

        // Si es un contacto existente, rellenar los campos con los datos actuales
        if (contact != null) {
            edtName.setText(contact.name)
            edtPhone.setText(contact.phone)
            edtEmail.setText(contact.email)
        }

        // Crear y mostrar el diálogo
        AlertDialog.Builder(this)
            .setTitle(if (contact == null) "Nuevo Contacto" else "Editar Contacto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newContact = Contact(
                    id = contact?.id, // Mantener el ID si es un contacto existente
                    name = edtName.text.toString(),
                    phone = edtPhone.text.toString(),
                    email = edtEmail.text.toString()
                )
                // Crear o actualizar según sea necesario
                if (contact == null) {
                    createContact(newContact)
                } else {
                    updateContact(newContact)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Método para crear un nuevo contacto
    private fun createContact(contact: Contact) {
        RetrofitClient.instance.createContact(contact).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    fetchContacts()  // Actualizar la lista después de crear el contacto
                    Toast.makeText(this@MainActivity, "Contacto creado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error al crear el contacto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al crear el contacto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Método para actualizar un contacto
    private fun updateContact(contact: Contact) {
        RetrofitClient.instance.updateContact(contact).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    fetchContacts()  // Actualizar la lista después de actualizar el contacto
                    Toast.makeText(this@MainActivity, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al actualizar el contacto", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Mostrar un diálogo de confirmación antes de eliminar un contacto
    private fun showDeleteConfirmationDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
            .setPositiveButton("Sí") { _, _ ->
                deleteContact(contact)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Método para eliminar un contacto
    private fun deleteContact(contact: Contact) {
        RetrofitClient.instance.deleteContact(contact).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    fetchContacts()  // Actualizar la lista después de eliminar el contacto
                    Toast.makeText(this@MainActivity, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error al eliminar el contacto", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

