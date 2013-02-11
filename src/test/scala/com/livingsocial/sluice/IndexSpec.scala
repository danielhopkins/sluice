package com.livingsocial.sluice

import org.apache.lucene.store._
import org.specs2.mutable.Specification

class IndexSpec extends Specification {
  "Creating a simple index" should {
    "example creating a test index" in {
      object index extends
      Index with
      MemoryBacked with
      SingleThreadedWrite with
      Search with
      BasicAnalyzer {}

      index.add("hi")
      index.toString must_== "RamDirectory"
    }

    "example creating a production index" in {
      object index extends
      Index with
      SingleThreadedWrite with
      Search with
      FileBacked with
      BasicAnalyzer {
        val directoryLocation = new java.io.File("foo")
      }

      index.add("hi")
      index.toString must_== "NioBacked"
    }

    "example of creating my own store and swapping it in" in {
      trait MyMemoryBacked extends Store {
        lazy val directory = new RAMDirectory {
          override def toString = "MyRamDirectory"
        }
      }

      object index extends
      Index with
      MyMemoryBacked with
      SingleThreadedWrite with
      Search with
      BasicAnalyzer {}

      index.toString must_== "MyRamDirectory"
    }
  }
}

