package com.minosiants
package youtube.extra

object StringUtils {

  def escapeHtml(text: String): String = {
    xml.Utility
      .escape(text)
      .replaceAll("'", "&#39;")
      .replaceAll("(\r\n|\n|\r)", "<br/>")
      .replaceAll("\\\\", "&#92;");
  }

}
