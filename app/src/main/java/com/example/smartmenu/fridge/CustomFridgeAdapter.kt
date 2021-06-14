package com.example.smartmenu.fridge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.smartmenu.R
import com.example.smartmenu.SingleLiveEvent


class CustomFridgeAdapter(
    private val listOfItems: List<FoodItem>,
    private val singleLiveEvent: SingleLiveEvent<Pair<Int, Boolean>>
) :
    BaseAdapter() {
    override fun getCount() = listOfItems.size

    override fun getItem(position: Int) = listOfItems[position]

    override fun getItemId(position: Int) = position.toLong()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView
        if (row == null) {
            val inflater: LayoutInflater = LayoutInflater.from(parent?.context)
            row = inflater.inflate(R.layout.custom_list_view, parent, false)
        }

        val checkedTextView = row
            ?.findViewById<View>(R.id.adaptertextview) as TextView
        val isCheckedTextView = row
            .findViewById<View>(R.id.adaptercheckbox) as CheckBox
        checkedTextView.text = listOfItems[position].foodName

        val myCheckChangList: CompoundButton.OnCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener {
                    _, isChecked -> // меняем данные товара (в корзине или нет)
                //listOfItems[position].isSelected = isChecked
                singleLiveEvent.postValue(Pair(position, isChecked))
            }

        isCheckedTextView.setOnCheckedChangeListener(myCheckChangList)
        val checked: Boolean = listOfItems[position].isSelected
        if (checked != null) {
            isCheckedTextView.isChecked = checked
        }

        return row
    }

    fun getCheckedItemPositions() = listOfItems.filter { it.isSelected }.map { it.foodName }
}