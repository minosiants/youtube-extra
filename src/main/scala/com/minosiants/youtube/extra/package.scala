package com.minosiants
package youtube

import java.io.File

package object extra {

  implicit final class FileOps(self: File) {
    def /(part: String): File = {
      new File(s"${self.getCanonicalPath}/$part")
    }
  }
}
