package ru.astrainteractive.aspekt.module.antiswear.data

import ru.astrainteractive.astralibs.krate.KrateFactory
import ru.astrainteractive.astralibs.krate.YamlKrateFactory
import java.io.File

internal object AntiSwearKrateFactory : KrateFactory by YamlKrateFactory(File("./.temp/antiswear"))
