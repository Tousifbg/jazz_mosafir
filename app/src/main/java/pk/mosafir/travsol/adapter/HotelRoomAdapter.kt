package pk.mosafir.travsol.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import pk.mosafir.travsol.R
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment.Companion.adults
import pk.mosafir.travsol.ui.book_hotel.BookHotelFragment.Companion.childList
import pk.mosafir.travsol.utils.gone
import pk.mosafir.travsol.utils.toast
import pk.mosafir.travsol.utils.visible

class HotelRoomAdapter(
    private val offers: MutableList<Int>
) :
    RecyclerView.Adapter<HotelRoomAdapter.OfferViewHolder>() {
    private lateinit var dialog: Dialog
    private lateinit var holder: OfferViewHolder
    private var child = 1
    private var pos = 0
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HotelRoomAdapter.OfferViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_room_list, parent, false)
        context = parent.context
        dialog = Dialog(parent.context, R.style.full_screen_dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return OfferViewHolder(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: HotelRoomAdapter.OfferViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.adultBox.setOnClickListener {
            pos = position
            this.holder = holder
            showDialog()
        }
        holder.childBox.setOnClickListener {
            this.holder = holder
            pos = position
            showDialogChild()
        }
        holder.childRelative1.setOnClickListener {
            child = 1
            showDialogRoomChild()
        }
        holder.childRelative2.setOnClickListener {
            child = 2
            showDialogRoomChild()
        }
        holder.childRelative3.setOnClickListener {
            child = 3
            showDialogRoomChild()
        }
        holder.childRelative4.setOnClickListener {
            child = 4
            showDialogRoomChild()
        }
    }

    override fun getItemCount(): Int = offers.size

    inner class OfferViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberOfAdults: TextView = itemView.findViewById(R.id.number_of_adults)
        val numberOfChildren: TextView = itemView.findViewById(R.id.number_of_child)

        val adultBox: RelativeLayout = itemView.findViewById(R.id.adult)
        val childBox: RelativeLayout = itemView.findViewById(R.id.child)

        val childConstraint1: ConstraintLayout = itemView.findViewById(R.id.child_constraint1)
        val childConstraint2: ConstraintLayout = itemView.findViewById(R.id.child_constraint2)
        val childConstraint3: ConstraintLayout = itemView.findViewById(R.id.child_constraint3)
        val childConstraint4: ConstraintLayout = itemView.findViewById(R.id.child_constraint4)

        val childRelative1: RelativeLayout = itemView.findViewById(R.id.child1)
        val childRelative2: RelativeLayout = itemView.findViewById(R.id.child2)
        val childRelative3: RelativeLayout = itemView.findViewById(R.id.child3)
        val childRelative4: RelativeLayout = itemView.findViewById(R.id.child4)

        val childAge1: TextView = itemView.findViewById(R.id.child_room1)
        val childAge2: TextView = itemView.findViewById(R.id.child_room2)
        val childAge3: TextView = itemView.findViewById(R.id.child_room3)
        val childAge4: TextView = itemView.findViewById(R.id.child_room4)

    }

    private fun showDialog() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_number6)
        val one = dialog.findViewById<RadioButton>(R.id.one)
        val two = dialog.findViewById<RadioButton>(R.id.two)
        val three = dialog.findViewById<RadioButton>(R.id.three)
        val four = dialog.findViewById<RadioButton>(R.id.four)
        val five = dialog.findViewById<RadioButton>(R.id.five)
        val six = dialog.findViewById<RadioButton>(R.id.six)
        one.setOnCheckedChangeListener { _, _ ->
            adults[pos] = 1
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                adults[pos] = 1
                dialog.dismiss()
            } else {
                holder.numberOfAdults.text = "1"
                adults[pos] = 1
                dialog.dismiss()
            }
        }
        two.setOnCheckedChangeListener { _, _ ->
            adults[pos] = 2
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                adults[pos] = 1
                dialog.dismiss()
            } else {
                holder.numberOfAdults.text = "2"
                adults[pos] = 2
                dialog.dismiss()
            }
        }
        three.setOnCheckedChangeListener { _, _ ->
            adults[pos] = 3
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                adults[pos] = 1
                dialog.dismiss()
            } else {
                holder.numberOfAdults.text = "3"
                adults[pos] = 3
                dialog.dismiss()
            }
        }
        four.setOnCheckedChangeListener { _, _ ->
            adults[pos] = 4
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                adults[pos] = 1
                dialog.dismiss()
            } else {
                holder.numberOfAdults.text = "4"
                adults[pos] = 4
                dialog.dismiss()
            }
        }
        five.setOnCheckedChangeListener { _, _ ->
            adults[pos] = 5
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                adults[pos] = 1
                dialog.dismiss()
            } else {
                holder.numberOfAdults.text = "5"
                adults[pos] = 5
                dialog.dismiss()
            }
        }
        six.setOnCheckedChangeListener { _, _ ->
            adults[pos] = 6
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                adults[pos] = 1
                dialog.dismiss()
            } else {
                holder.numberOfAdults.text = "6"
                adults[pos] = 6
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun showDialogChild() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_number0to4)
        val one = dialog.findViewById<RadioButton>(R.id.one)
        val two = dialog.findViewById<RadioButton>(R.id.two)
        val three = dialog.findViewById<RadioButton>(R.id.three)
        val four = dialog.findViewById<RadioButton>(R.id.four)
        val zero = dialog.findViewById<RadioButton>(R.id.zeero)
        one.setOnCheckedChangeListener { _, _ ->
            childList[pos] = 1
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                childList[pos] = 0
                dialog.dismiss()
            } else {
                holder.numberOfChildren.text = "1"
                holder.childConstraint1.visible()
                dialog.dismiss()
            }
        }
        two.setOnCheckedChangeListener { _, _ ->
            childList[pos] = 2
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                childList[pos] = 0
                dialog.dismiss()
            } else {
                holder.numberOfChildren.text = "2"
                holder.childConstraint1.visible()
                holder.childConstraint2.visible()
                dialog.dismiss()
            }
        }
        three.setOnCheckedChangeListener { _, _ ->
            childList[pos] = 3
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                childList[pos] = 0
                dialog.dismiss()
            } else {
                holder.numberOfChildren.text = "3"
                childList[pos] = 3
                holder.childConstraint1.visible()
                holder.childConstraint2.visible()
                holder.childConstraint3.visible()
                dialog.dismiss()
            }
        }
        four.setOnCheckedChangeListener { _, _ ->
            childList[pos] = 4
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                childList[pos] = 0
                dialog.dismiss()
            } else {
                holder.numberOfChildren.text = "4"
                childList[pos] = 4
                holder.childConstraint1.visible()
                holder.childConstraint2.visible()
                holder.childConstraint3.visible()
                holder.childConstraint4.visible()
                dialog.dismiss()
            }
        }
        zero.setOnCheckedChangeListener { _, _ ->
            childList[pos] = 0
            if (childList[pos] + adults[pos] > 6) {
                context.toast("No more than 6 in a room")
                childList[pos] = 0
                dialog.dismiss()
            } else {
                holder.numberOfChildren.text = "0"
                childList[pos] = 0
                holder.childConstraint1.gone()
                holder.childConstraint2.gone()
                holder.childConstraint3.gone()
                holder.childConstraint4.gone()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showDialogRoomChild() {
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.cutom_dialog_number2to16)
        val two = dialog.findViewById<RadioButton>(R.id.two)
        val three = dialog.findViewById<RadioButton>(R.id.three)
        val four = dialog.findViewById<RadioButton>(R.id.four)
        val five = dialog.findViewById<RadioButton>(R.id.five)
        val six = dialog.findViewById<RadioButton>(R.id.six)
        val seven = dialog.findViewById<RadioButton>(R.id.seven)
        val eight = dialog.findViewById<RadioButton>(R.id.eight)
        val nine = dialog.findViewById<RadioButton>(R.id.nine)
        val ten = dialog.findViewById<RadioButton>(R.id.ten)
        val eleven = dialog.findViewById<RadioButton>(R.id.eleven)
        val tvelve = dialog.findViewById<RadioButton>(R.id.twelve)
        val thirteen = dialog.findViewById<RadioButton>(R.id.thirteen)
        val fourteen = dialog.findViewById<RadioButton>(R.id.fourteen)
        val fifteen = dialog.findViewById<RadioButton>(R.id.fifteen)
        val sixteen = dialog.findViewById<RadioButton>(R.id.sixteen)
        two.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "2"
                }
                2 -> {
                    holder.childAge2.text = "2"
                }
                3 -> {
                    holder.childAge3.text = "2"
                }
                4 -> {
                    holder.childAge4.text = "2"
                }
            }
            dialog.dismiss()
        }
        three.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "3"
                }
                2 -> {
                    holder.childAge2.text = "3"
                }
                3 -> {
                    holder.childAge3.text = "3"
                }
                4 -> {
                    holder.childAge4.text = "3"
                }
            }
            dialog.dismiss()
        }
        four.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "4"
                }
                2 -> {
                    holder.childAge2.text = "4"
                }
                3 -> {
                    holder.childAge3.text = "4"
                }
                4 -> {
                    holder.childAge4.text = "4"
                }
            }
            dialog.dismiss()
        }
        five.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "5"
                }
                2 -> {
                    holder.childAge2.text = "5"
                }
                3 -> {
                    holder.childAge3.text = "5"
                }
                4 -> {
                    holder.childAge4.text = "5"
                }
            }
            dialog.dismiss()
        }
        six.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "6"
                }
                2 -> {
                    holder.childAge2.text = "6"
                }
                3 -> {
                    holder.childAge3.text = "6"
                }
                4 -> {
                    holder.childAge4.text = "6"
                }
            }
            dialog.dismiss()
        }
        seven.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "7"
                }
                2 -> {
                    holder.childAge2.text = "7"
                }
                3 -> {
                    holder.childAge3.text = "7"
                }
                4 -> {
                    holder.childAge4.text = "7"
                }
            }
            dialog.dismiss()
        }
        eight.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "8"
                }
                2 -> {
                    holder.childAge2.text = "8"
                }
                3 -> {
                    holder.childAge3.text = "8"
                }
                4 -> {
                    holder.childAge4.text = "8"
                }
            }
            dialog.dismiss()
        }
        nine.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "9"
                }
                2 -> {
                    holder.childAge2.text = "9"
                }
                3 -> {
                    holder.childAge3.text = "9"
                }
                4 -> {
                    holder.childAge4.text = "9"
                }
            }
            dialog.dismiss()
        }
        ten.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "10"
                }
                2 -> {
                    holder.childAge2.text = "10"
                }
                3 -> {
                    holder.childAge3.text = "10"
                }
                4 -> {
                    holder.childAge4.text = "10"
                }
            }
            dialog.dismiss()
        }
        eleven.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "11"
                }
                2 -> {
                    holder.childAge2.text = "11"
                }
                3 -> {
                    holder.childAge3.text = "11"
                }
                4 -> {
                    holder.childAge4.text = "11"
                }
            }
            dialog.dismiss()
        }
        tvelve.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "12"
                }
                2 -> {
                    holder.childAge2.text = "12"
                }
                3 -> {
                    holder.childAge3.text = "12"
                }
                4 -> {
                    holder.childAge4.text = "12"
                }
            }
            dialog.dismiss()
        }
        thirteen.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "13"
                }
                2 -> {
                    holder.childAge2.text = "13"
                }
                3 -> {
                    holder.childAge3.text = "13"
                }
                4 -> {
                    holder.childAge4.text = "13"
                }
            }
            dialog.dismiss()
        }
        fourteen.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "14"
                }
                2 -> {
                    holder.childAge2.text = "14"
                }
                3 -> {
                    holder.childAge3.text = "14"
                }
                4 -> {
                    holder.childAge4.text = "14"
                }
            }
            dialog.dismiss()
        }
        fifteen.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "15"
                }
                2 -> {
                    holder.childAge2.text = "15"
                }
                3 -> {
                    holder.childAge3.text = "15"
                }
                4 -> {
                    holder.childAge4.text = "15"
                }
            }
            dialog.dismiss()
        }
        sixteen.setOnCheckedChangeListener { _, _ ->
            when (child) {
                1 -> {
                    holder.childAge1.text = "16"
                }
                2 -> {
                    holder.childAge2.text = "16"
                }
                3 -> {
                    holder.childAge3.text = "16"
                }
                4 -> {
                    holder.childAge4.text = "16"
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}