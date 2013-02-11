package com.livingsocial.sluice

import org.apache.lucene.store._
import org.apache.lucene.document._
import org.apache.lucene.search._
import org.apache.lucene.index._
import org.apache.lucene.analysis._
import org.apache.lucene.analysis.standard._
import Field.Store.{YES => STORED, NO => NOT_STORED}

trait Index { this: Write with Store =>

  def add(d: String) {
    // Not really a proper document yet
    val doc = new Document
    doc.add(new StringField("deal_type", d, STORED))

    withWriter { w =>
      w.addDocument(doc)
    }
  }

  override def toString = directory.toString

  def startup() {
    println("Started")
  }

  def stutdown() {
    println("Stopped")
  }

  startup()
  // TODO get Shutdown working
}

trait Search { this: Store with Write =>
  // TODO: not sure what this is creating, need docs
  // why is it taking a writer now?
  def searcher = new SearcherManager(writer, true, null)
  def reopen {
    // Apparently this disappeared
    // searcher.maybeReopen
    println("Reopened!")
  }

  def withReader[A](f: IndexSearcher => A ) = {
    val s = searcher.acquire
    try f(s) finally {
      searcher.release(s)
    }
  }
}

trait Write { this: Search with IndexingAnalyzer with Store =>
  def config: IndexWriterConfig
  def writer: IndexWriter

  def withWriter[A](f: IndexWriter => A): A = {
    val w = writer

    try f(w) finally {
      w.commit()
      w.close()
      reopen
    }
  }
}

trait SingleThreadedWrite extends Write with Versioned { this: Search with IndexingAnalyzer with Store =>
  def config = {
    val config = new IndexWriterConfig(version, analyzer)
    config.setRAMBufferSizeMB(48d)
    config
  }

  def writer = new IndexWriter(directory, config)
}

trait IndexingAnalyzer {
  def analyzer: Analyzer
}

trait BasicAnalyzer extends IndexingAnalyzer with Versioned {
  def analyzer: Analyzer = new StandardAnalyzer(version)
}

trait Versioned {
  val version = org.apache.lucene.util.Version.LUCENE_40
}
