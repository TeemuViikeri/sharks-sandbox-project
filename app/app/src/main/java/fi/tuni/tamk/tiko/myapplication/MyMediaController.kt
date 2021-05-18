package fi.tuni.tamk.tiko.myapplication

import android.content.Context
import android.view.View
import android.widget.MediaController

class MyMediaController(context: Context, anchor: View?) : MediaController(context) {
    init {
        super.setAnchorView(anchor)
    }

    constructor(context: Context) : this(context, null)

    override fun setAnchorView(view: View) { }
}