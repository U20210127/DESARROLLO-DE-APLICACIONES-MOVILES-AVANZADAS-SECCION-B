package com.example.pr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private var contacts: List<Contact>,
    private val onEdit: (Contact) -> Unit,
    private val onDelete: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.txtName)
        val phone = view.findViewById<TextView>(R.id.txtPhone)
        val email = view.findViewById<TextView>(R.id.txtEmail)
        val btnEdit = view.findViewById<Button>(R.id.btnEdit)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone
        holder.email.text = contact.email

        holder.btnEdit.setOnClickListener { onEdit(contact) }
        holder.btnDelete.setOnClickListener { onDelete(contact) }
    }

    fun updateList(newContacts: List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }
}
