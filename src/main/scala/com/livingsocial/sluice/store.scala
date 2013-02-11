package com.livingsocial.sluice

import org.apache.lucene.store._

trait Store {
  def directory: Directory
}

trait MemoryBacked extends Store {
  lazy val directory = new RAMDirectory {
    override def toString = "RamDirectory"
  }
}

trait FileBacked extends Store {
  def directoryLocation: java.io.File
  lazy val directory = new NIOFSDirectory(directoryLocation) {
    override def toString = "NioBacked"
  }
}
