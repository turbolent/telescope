package com.turbolent.questionServer

import java.nio.file.Path


case class Configuration(port: Int = 8080,
                         taggerModelPath: Path = null,
                         lemmatizerModelPath: Path = null)
