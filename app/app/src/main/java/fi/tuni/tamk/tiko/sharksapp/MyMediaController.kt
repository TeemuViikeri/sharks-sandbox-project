package fi.tuni.tamk.tiko.sharksapp

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