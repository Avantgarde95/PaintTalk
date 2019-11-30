package com.github.avantgarde95.painttalk.view

import com.github.avantgarde95.painttalk.SimpleEvent
import com.github.avantgarde95.painttalk.Util
import java.awt.Dimension
import java.awt.Image
import javax.imageio.ImageIO
import javax.swing.*

class ControlPanel : JPanel() {
    private val openButton = JButton("Open").apply {
        setIconFromPath(this, "Image/Open.jpg")
        addActionListener { openRequestEvent.fire(Unit) }
    }

    private val saveButton = JButton("Save").apply {
        setIconFromPath(this, "Image/Save.png")
        addActionListener { saveRequestEvent.fire(Unit) }
    }

    private val exportButton = JButton("Export").apply {
        setIconFromPath(this, "Image/Export.png")
        addActionListener { exportRequestEvent.fire(Unit) }
    }

    private val drawButton = JButton("Draw").apply {
        setIconFromPath(this, "Image/Draw.jpg")
        addActionListener { drawRequestEvent.fire(Unit) }
    }

    val openRequestEvent = SimpleEvent<Unit>()
    val saveRequestEvent = SimpleEvent<Unit>()
    val exportRequestEvent = SimpleEvent<Unit>()
    val drawRequestEvent = SimpleEvent<Unit>()

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        border = BorderFactory.createEmptyBorder(5, 5, 5, 5)

        add(openButton)
        add(Box.createRigidArea(Dimension(5, 0)))
        add(saveButton)
        add(Box.createRigidArea(Dimension(5, 0)))
        add(exportButton)
        add(Box.createRigidArea(Dimension(5, 0)))
        add(drawButton)
    }

    private fun setIconFromPath(button: JButton, path: String) {
        val image = ImageIO.read(Util.getResourceAsStream(path))
        val scaledImage = image.getScaledInstance(20, 20, Image.SCALE_SMOOTH)

        button.apply {
            horizontalTextPosition = JButton.CENTER
            verticalTextPosition = JButton.BOTTOM
            icon = ImageIcon(scaledImage)
        }
    }
}