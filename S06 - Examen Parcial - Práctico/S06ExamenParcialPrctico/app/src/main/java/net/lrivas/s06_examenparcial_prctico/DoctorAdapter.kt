package net.lrivas.s06_examenparcial_prctico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(
    private val doctorList: List<Doctor>,
    private val onItemClick: (Doctor) -> Unit
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardLayout: LinearLayout = itemView.findViewById(R.id.doctorCardLayout)
        val doctorImage: ImageView = itemView.findViewById(R.id.doctorImage)
        val doctorName: TextView = itemView.findViewById(R.id.doctorName)
        val doctorSpecialty: TextView = itemView.findViewById(R.id.doctorSpecialty)
        val doctorExperience: TextView = itemView.findViewById(R.id.doctorExperience)
        val doctorPatients: TextView = itemView.findViewById(R.id.doctorPatients)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]

        holder.doctorName.text = doctor.name
        holder.doctorSpecialty.text = doctor.specialty
        holder.doctorExperience.text = doctor.experience
        holder.doctorPatients.text = doctor.patients
        holder.doctorImage.setImageResource(doctor.imageResId)

        // Set card background color
        holder.cardLayout.setBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, doctor.color)
        )

        // Set item click listener
        holder.itemView.setOnClickListener {
            onItemClick(doctor)
        }
    }

    override fun getItemCount(): Int = doctorList.size
}