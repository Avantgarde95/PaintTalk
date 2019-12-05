package com.github.avantgarde95.painttalk

import java.awt.Font
import javax.swing.UIManager

fun main() {
    Font.createFont(
        Font.TRUETYPE_FONT,
        Util.getResourceAsStream("Font/Nanum_Gothic/NanumGothic-ExtraBold.ttf")
    ).deriveFont(16f).run {
        UIManager.put("Button.font", this)
        UIManager.put("TabbedPane.font", this)
        UIManager.put("TitledBorder.font", this)
        UIManager.put("List.font", this)
        UIManager.put("Label.font", this)
    }

    Font.createFont(
        Font.TRUETYPE_FONT,
        Util.getResourceAsStream("Font/Nanum_Gothic_Coding/NanumGothicCoding-Bold.ttf")
    ).deriveFont(16f).run {
        UIManager.put("TextArea.font", this)
        UIManager.put("TextField.font", this)
    }

    App().start()
}
