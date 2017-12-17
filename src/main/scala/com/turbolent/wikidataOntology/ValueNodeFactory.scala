package com.turbolent.wikidataOntology

import com.turbolent.questionCompiler.graph.{Count, Descending}
import com.turbolent.questionParser.Token
import com.turbolent.wikidataOntology.AdjectiveEdgeFactory.makeAdjectiveEdge
import com.turbolent.wikidataOntology.Tokens._

import scala.collection.mutable


object ValueNodeFactory {

  def instanceOrOccupation(item: Item): NodeFactory =
    (node, env) =>
      node.out(P.isA, item)
          .or(out(P.hasOccupation, item))


  val factories: mutable.Map[String, NodeFactory] =
    mutable.Map(
      "movie" -> Q.movie,
      "mountain" -> Q.mountain,
      "president" -> { (node, env) =>
        node.out(P.holdsPosition, Q.president)
      },
      "author" -> { (node, env) =>
        node.in(env.newNode(), P.hasAuthor)
      },
      "book" -> Q.book,
      "language" -> Q.language,
      "instrument" -> Q.musicalInstrument,
      "city" -> Q.city,
      "child" -> { (node, env) =>
        node.in(env.newNode(), P.hasChild)
      },
      "planet" -> Q.planet,
      "humans" -> Q.human,
      "people" -> Q.human,
      "person" -> Q.human,
      "country" -> Q.country,
      "year" -> Q.year,
      "woman" -> { (node, env) =>
        node.out(P.hasGender, Q.female)
      },
      "man" -> { (node, env) =>
        node.out(P.hasGender, Q.male)
      },
      "university" -> { (node, env) =>
        val university = env.newNode()
            .out(P.isA, Q.university)
        node.out(P.isA, Q.university)
            .or(out(P.isPartOf, university))
      },
      "politician" -> instanceOrOccupation(Q.politician),
      "actor" -> instanceOrOccupation(Q.actor),
      "painter" -> instanceOrOccupation(Q.painter),
      "writer" -> instanceOrOccupation(Q.writer),
      "journalist" -> instanceOrOccupation(Q.journalist),
      "singer" -> instanceOrOccupation(Q.singer),
      "composer" -> instanceOrOccupation(Q.composer),
      "priest" -> instanceOrOccupation(Q.priest),
      "baseball player" -> instanceOrOccupation(Q.baseballPlayer),
      "poet" -> instanceOrOccupation(Q.poet),
      "lawyer" -> instanceOrOccupation(Q.lawyer),
      "athletics competitor" -> instanceOrOccupation(Q.athleticsCompetitor),
      "historian" -> instanceOrOccupation(Q.historian),
      "film director" -> instanceOrOccupation(Q.filmDirector),
      "screenwriter" -> instanceOrOccupation(Q.screenwriter),
      "ice hockey player" -> instanceOrOccupation(Q.iceHockeyPlayer),
      "architect" -> instanceOrOccupation(Q.architect),
      "diplomat" -> instanceOrOccupation(Q.diplomat),
      "cricketer" -> instanceOrOccupation(Q.cricketer),
      "musician" -> instanceOrOccupation(Q.musician),
      "engineer" -> instanceOrOccupation(Q.engineer),
      "basketball player" -> instanceOrOccupation(Q.basketballPlayer),
      "sculptor" -> instanceOrOccupation(Q.sculptor),
      "bicycle racer" -> instanceOrOccupation(Q.bicycleRacer),
      "officer" -> instanceOrOccupation(Q.officer),
      "novelist" -> instanceOrOccupation(Q.novelist),
      "rugby union player" -> instanceOrOccupation(Q.rugbyUnionPlayer),
      "judge" -> instanceOrOccupation(Q.judge),
      "botanist" -> instanceOrOccupation(Q.botanist),
      "photographer" -> instanceOrOccupation(Q.photographer),
      "theologian" -> instanceOrOccupation(Q.theologian),
      "physician" -> instanceOrOccupation(Q.physician),
      "sportsperson" -> instanceOrOccupation(Q.sportsperson),
      "economist" -> instanceOrOccupation(Q.economist),
      "mathematician" -> instanceOrOccupation(Q.mathematician),
      "artist" -> instanceOrOccupation(Q.artist),
      "conductor" -> instanceOrOccupation(Q.conductor),
      "philosopher" -> instanceOrOccupation(Q.philosopher),
      "jurist" -> instanceOrOccupation(Q.jurist),
      "singer-songwriter" -> instanceOrOccupation(Q.singerSongwriter),
      "translator" -> instanceOrOccupation(Q.translator),
      "film producer" -> instanceOrOccupation(Q.filmProducer),
      "entrepreneur" -> instanceOrOccupation(Q.entrepreneur),
      "physicist" -> instanceOrOccupation(Q.physicist),
      "jazz musician" -> instanceOrOccupation(Q.jazzMusician),
      "television presenter" -> instanceOrOccupation(Q.televisionPresenter),
      "linguist" -> instanceOrOccupation(Q.linguist),
      "rugby league player" -> instanceOrOccupation(Q.rugbyLeaguePlayer),
      "seiyū" -> instanceOrOccupation(Q.seiyu),
      "opera singer" -> instanceOrOccupation(Q.operaSinger),
      "chemist" -> instanceOrOccupation(Q.chemist),
      "music educator" -> instanceOrOccupation(Q.musicEducator),
      "tennis player" -> instanceOrOccupation(Q.tennisPlayer),
      "educationist" -> instanceOrOccupation(Q.educationist),
      "rower" -> instanceOrOccupation(Q.rower),
      "entomologist" -> instanceOrOccupation(Q.entomologist),
      "teacher" -> instanceOrOccupation(Q.teacher),
      "model" -> instanceOrOccupation(Q.model),
      "pianist" -> instanceOrOccupation(Q.pianist),
      "soldier" -> instanceOrOccupation(Q.soldier),
      "musicologist" -> instanceOrOccupation(Q.musicologist),
      "boxer" -> instanceOrOccupation(Q.boxer),
      "voice actor" -> instanceOrOccupation(Q.voiceActor),
      "anthropologist" -> instanceOrOccupation(Q.anthropologist),
      "archaeologist" -> instanceOrOccupation(Q.archaeologist),
      "illustrator" -> instanceOrOccupation(Q.illustrator),
      "handball player" -> instanceOrOccupation(Q.handballPlayer),
      "mangaka" -> instanceOrOccupation(Q.mangaka),
      "playwright" -> instanceOrOccupation(Q.playwright),
      "art historian" -> instanceOrOccupation(Q.artHistorian),
      "songwriter" -> instanceOrOccupation(Q.songwriter),
      "chess player" -> instanceOrOccupation(Q.chessPlayer),
      "film actor" -> instanceOrOccupation(Q.filmActor),
      "astronomer" -> instanceOrOccupation(Q.astronomer),
      "announcer" -> instanceOrOccupation(Q.announcer),
      "basketball coach" -> instanceOrOccupation(Q.basketballCoach),
      "badminton player" -> instanceOrOccupation(Q.badmintonPlayer),
      "explorer" -> instanceOrOccupation(Q.explorer),
      "university professor" -> instanceOrOccupation(Q.universityProfessor),
      "fencer" -> instanceOrOccupation(Q.fencer),
      "racing driver" -> instanceOrOccupation(Q.racingDriver),
      "golfer" -> instanceOrOccupation(Q.golfer),
      "businessperson" -> instanceOrOccupation(Q.businessperson),
      "professor" -> instanceOrOccupation(Q.professor),
      "publisher" -> instanceOrOccupation(Q.publisher),
      "radio host" -> instanceOrOccupation(Q.radioHost),
      "psychologist" -> instanceOrOccupation(Q.psychologist),
      "guitarist" -> instanceOrOccupation(Q.guitarist),
      "record producer" -> instanceOrOccupation(Q.recordProducer),
      "swimmer" -> instanceOrOccupation(Q.swimmer),
      "volleyball player" -> instanceOrOccupation(Q.volleyballPlayer),
      "private banker" -> instanceOrOccupation(Q.privateBanker),
      "librarian" -> instanceOrOccupation(Q.librarian),
      "amateur wrestler" -> instanceOrOccupation(Q.amateurWrestler),
      "esperantist" -> instanceOrOccupation(Q.esperantist),
      "sociologist" -> instanceOrOccupation(Q.sociologist),
      "zoologist" -> instanceOrOccupation(Q.zoologist),
      "biologist" -> instanceOrOccupation(Q.biologist),
      "television actor" -> instanceOrOccupation(Q.televisionActor),
      "cinematographer" -> instanceOrOccupation(Q.cinematographer),
      "field hockey player" -> instanceOrOccupation(Q.fieldHockeyPlayer),
      "computer scientist" -> instanceOrOccupation(Q.computerScientist),
      "rabbi" -> instanceOrOccupation(Q.rabbi),
      "military personnel" -> instanceOrOccupation(Q.militaryPersonnel),
      "motorcycle racer" -> instanceOrOccupation(Q.motorcycleRacer),
      "sprinter" -> instanceOrOccupation(Q.sprinter),
      "theatre director" -> instanceOrOccupation(Q.theatreDirector),
      "choreographer" -> instanceOrOccupation(Q.choreographer),
      "sport shooter" -> instanceOrOccupation(Q.sportShooter),
      "pornographic actor" -> instanceOrOccupation(Q.pornographicActor),
      "psychiatrist" -> instanceOrOccupation(Q.psychiatrist),
      "alpine skier" -> instanceOrOccupation(Q.alpineSkier),
      "surgeon" -> instanceOrOccupation(Q.surgeon),
      "dancer" -> instanceOrOccupation(Q.dancer),
      "rapper" -> instanceOrOccupation(Q.rapper),
      "political scientist" -> instanceOrOccupation(Q.politicalScientist),
      "comedian" -> instanceOrOccupation(Q.comedian),
      "biathlete" -> instanceOrOccupation(Q.biathlete),
      "disc jockey" -> instanceOrOccupation(Q.discJockey),
      "association football manager" -> instanceOrOccupation(Q.associationFootballManager),
      "film editor" -> instanceOrOccupation(Q.filmEditor),
      "judoka" -> instanceOrOccupation(Q.judoka),
      "autobiographer" -> instanceOrOccupation(Q.autobiographer),
      "ski jumper" -> instanceOrOccupation(Q.skiJumper),
      "organist" -> instanceOrOccupation(Q.organist),
      "missionary" -> instanceOrOccupation(Q.missionary),
      "aviator" -> instanceOrOccupation(Q.aviator),
      "scientist" -> instanceOrOccupation(Q.scientist),
      "hurler" -> instanceOrOccupation(Q.hurler),
      "classical philologist" -> instanceOrOccupation(Q.classicalPhilologist),
      "philologist" -> instanceOrOccupation(Q.philologist),
      "musher" -> instanceOrOccupation(Q.musher),
      "member of parliament" -> instanceOrOccupation(Q.memberOfParliament),
      "paleontologist" -> instanceOrOccupation(Q.paleontologist),
      "director" -> instanceOrOccupation(Q.director),
      "speed skater" -> instanceOrOccupation(Q.speedSkater),
      "geologist" -> instanceOrOccupation(Q.geologist),
      "jockey" -> instanceOrOccupation(Q.jockey),
      "ornithologist" -> instanceOrOccupation(Q.ornithologist),
      "civil engineer" -> instanceOrOccupation(Q.civilEngineer),
      "comics artist" -> instanceOrOccupation(Q.comicsArtist),
      "presenter" -> instanceOrOccupation(Q.presenter),
      "graphic designer" -> instanceOrOccupation(Q.graphicDesigner),
      "professional wrestler" -> instanceOrOccupation(Q.professionalWrestler),
      "television director" -> instanceOrOccupation(Q.televisionDirector),
      "rikishi" -> instanceOrOccupation(Q.rikishi),
      "Catholic priest" -> instanceOrOccupation(Q.catholicPriest),
      "Gaelic football player" -> instanceOrOccupation(Q.gaelicFootballPlayer),
      "essayist" -> instanceOrOccupation(Q.essayist),
      "mixed martial artist" -> instanceOrOccupation(Q.mixedMartialArtist),
      "farmer" -> instanceOrOccupation(Q.farmer),
      "table tennis player" -> instanceOrOccupation(Q.tableTennisPlayer),
      "inventor" -> instanceOrOccupation(Q.inventor),
      "merchant" -> instanceOrOccupation(Q.merchant),
      "archivist" -> instanceOrOccupation(Q.archivist),
      "animator" -> instanceOrOccupation(Q.animator),
      "geographer" -> instanceOrOccupation(Q.geographer),
      "police officer" -> instanceOrOccupation(Q.policeOfficer),
      "head coach" -> instanceOrOccupation(Q.headCoach),
      "cross-country skier" -> instanceOrOccupation(Q.crossCountrySkier),
      "editor" -> instanceOrOccupation(Q.editor),
      "marathon runner" -> instanceOrOccupation(Q.marathonRunner),
      "sports coach" -> instanceOrOccupation(Q.sportsCoach),
      "biochemist" -> instanceOrOccupation(Q.biochemist),
      "samurai" -> instanceOrOccupation(Q.samurai),
      "designer" -> instanceOrOccupation(Q.designer),
      "violinist" -> instanceOrOccupation(Q.violinist),
      "television producer" -> instanceOrOccupation(Q.televisionProducer),
      "ballet dancer" -> instanceOrOccupation(Q.balletDancer),
      "naturalist" -> instanceOrOccupation(Q.naturalist),
      "herpetologist" -> instanceOrOccupation(Q.herpetologist),
      "saxophonist" -> instanceOrOccupation(Q.saxophonist),
      "engraver" -> instanceOrOccupation(Q.engraver),
      "chef" -> instanceOrOccupation(Q.chef),
      "drummer" -> instanceOrOccupation(Q.drummer),
      "human rights activist" -> instanceOrOccupation(Q.humanRightsActivist),
      "audio engineer" -> instanceOrOccupation(Q.audioEngineer),
      "cartoonist" -> instanceOrOccupation(Q.cartoonist),
      "ichthyologist" -> instanceOrOccupation(Q.ichthyologist),
      "historian of modern age" -> instanceOrOccupation(Q.historianOfModernAge),
      "fashion designer" -> instanceOrOccupation(Q.fashionDesigner),
      "presbyter" -> instanceOrOccupation(Q.presbyter),
      "mycologist" -> instanceOrOccupation(Q.mycologist),
      "statistician" -> instanceOrOccupation(Q.statistician),
      "long-distance runner" -> instanceOrOccupation(Q.longDistanceRunner),
      "bishop" -> instanceOrOccupation(Q.bishop),
      "curler" -> instanceOrOccupation(Q.curler),
      "nurse" -> instanceOrOccupation(Q.nurse),
      "mountaineer" -> instanceOrOccupation(Q.mountaineer),
      "cleric" -> instanceOrOccupation(Q.cleric),
      "bandleader" -> instanceOrOccupation(Q.bandleader),
      "contributing editor" -> instanceOrOccupation(Q.contributingEditor),
      "librettist" -> instanceOrOccupation(Q.librettist),
      "lichenologist" -> instanceOrOccupation(Q.lichenologist),
      "opinion journalist" -> instanceOrOccupation(Q.opinionJournalist),
      "monk" -> instanceOrOccupation(Q.monk),
      "medievalist" -> instanceOrOccupation(Q.medievalist),
      "art collector" -> instanceOrOccupation(Q.artCollector),
      "civil servant" -> instanceOrOccupation(Q.civilServant),
      "cricket umpire" -> instanceOrOccupation(Q.cricketUmpire),
      "dub actor" -> instanceOrOccupation(Q.dubActor),
      "manager" -> instanceOrOccupation(Q.manager),
      "middle-distance runner" -> instanceOrOccupation(Q.middleDistanceRunner),
      "draughtsperson" -> instanceOrOccupation(Q.draughtsperson),
      "literary critic" -> instanceOrOccupation(Q.literaryCritic),
      "sailor" -> instanceOrOccupation(Q.sailor),
      "racecar driver" -> instanceOrOccupation(Q.racecarDriver),
      "magistrate" -> instanceOrOccupation(Q.magistrate),
      "Rakugoka" -> instanceOrOccupation(Q.rakugoka),
      "condottiero" -> instanceOrOccupation(Q.condottiero),
      "weightlifter" -> instanceOrOccupation(Q.weightlifter),
      "specialist in literature" -> instanceOrOccupation(Q.specialistInLiterature),
      "trumpeter" -> instanceOrOccupation(Q.trumpeter),
      "choir director" -> instanceOrOccupation(Q.choirDirector),
      "lexicographer" -> instanceOrOccupation(Q.lexicographer),
      "cartographer" -> instanceOrOccupation(Q.cartographer),
      "aerospace engineer" -> instanceOrOccupation(Q.aerospaceEngineer),
      "pharmacist" -> instanceOrOccupation(Q.pharmacist),
      "church historian" -> instanceOrOccupation(Q.churchHistorian),
      "astronaut" -> instanceOrOccupation(Q.astronaut),
      "organ maker" -> instanceOrOccupation(Q.organMaker),
      "columnist" -> instanceOrOccupation(Q.columnist),
      "pastor" -> instanceOrOccupation(Q.pastor),
      "military physician" -> instanceOrOccupation(Q.militaryPhysician),
      "mineralogist" -> instanceOrOccupation(Q.mineralogist),
      "lithographer" -> instanceOrOccupation(Q.lithographer),
      "bassist" -> instanceOrOccupation(Q.bassist),
      "artistic gymnast" -> instanceOrOccupation(Q.artisticGymnast),
      "news presenter" -> instanceOrOccupation(Q.newsPresenter),
      "dentist" -> instanceOrOccupation(Q.dentist),
      "meteorologist" -> instanceOrOccupation(Q.meteorologist),
      "photojournalist" -> instanceOrOccupation(Q.photojournalist),
      "prehistorian" -> instanceOrOccupation(Q.prehistorian),
      "neuroscientist" -> instanceOrOccupation(Q.neuroscientist),
      "mayor" -> instanceOrOccupation(Q.mayor),
      "veterinarian" -> instanceOrOccupation(Q.veterinarian),
      "metallurgist" -> instanceOrOccupation(Q.metallurgist),
      "beach volleyball player" -> instanceOrOccupation(Q.beachVolleyballPlayer),
      "futsal player" -> instanceOrOccupation(Q.futsalPlayer),
      "geneticist" -> instanceOrOccupation(Q.geneticist),
      "circus performer" -> instanceOrOccupation(Q.circusPerformer),
      "caricaturist" -> instanceOrOccupation(Q.caricaturist),
      "sovereign" -> instanceOrOccupation(Q.sovereign),
      "literary historian" -> instanceOrOccupation(Q.literaryHistorian),
      "Formula One driver" -> instanceOrOccupation(Q.formulaOneDriver),
      "regional historian" -> instanceOrOccupation(Q.regionalHistorian),
      "ophthalmologist" -> instanceOrOccupation(Q.ophthalmologist),
      "diarist" -> instanceOrOccupation(Q.diarist),
      "abbot" -> instanceOrOccupation(Q.abbot),
      "go player" -> instanceOrOccupation(Q.goPlayer),
      "cook" -> instanceOrOccupation(Q.cook),
      "squash player" -> instanceOrOccupation(Q.squashPlayer),
      "curator" -> instanceOrOccupation(Q.curator),
      "ceramist" -> instanceOrOccupation(Q.ceramist),
      "minister" -> instanceOrOccupation(Q.minister),
      "poker player" -> instanceOrOccupation(Q.pokerPlayer),
      "water polo player" -> instanceOrOccupation(Q.waterPoloPlayer),
      "psychotherapist" -> instanceOrOccupation(Q.psychotherapist),
      "archbishop" -> instanceOrOccupation(Q.archbishop),
      "track cyclist" -> instanceOrOccupation(Q.trackCyclist),
      "horse trainer" -> instanceOrOccupation(Q.horseTrainer),
      "internist" -> instanceOrOccupation(Q.internist),
      "financier" -> instanceOrOccupation(Q.financier),
      "political commissar" -> instanceOrOccupation(Q.politicalCommissar),
      "Heimatforscher" -> instanceOrOccupation(Q.heimatforscher),
      "archer" -> instanceOrOccupation(Q.archer),
      "germanist" -> instanceOrOccupation(Q.germanist),
      "lacrosse player" -> instanceOrOccupation(Q.lacrossePlayer),
      "magician" -> instanceOrOccupation(Q.magician),
      "bandy player" -> instanceOrOccupation(Q.bandyPlayer),
      "camera operator" -> instanceOrOccupation(Q.cameraOperator),
      "music theorist" -> instanceOrOccupation(Q.musicTheorist),
      "barrister" -> instanceOrOccupation(Q.barrister),
      "javelin thrower" -> instanceOrOccupation(Q.javelinThrower),
      "arachnologist" -> instanceOrOccupation(Q.arachnologist),
      "taekwondo athlete" -> instanceOrOccupation(Q.taekwondoAthlete),
      "printer" -> instanceOrOccupation(Q.printer),
      "scenographer" -> instanceOrOccupation(Q.scenographer),
      "clarinetist" -> instanceOrOccupation(Q.clarinetist),
      "pro gamer" -> instanceOrOccupation(Q.proGamer),
      "snooker player" -> instanceOrOccupation(Q.snookerPlayer),
      "legal historian" -> instanceOrOccupation(Q.legalHistorian),
      "cabaret artist" -> instanceOrOccupation(Q.cabaretArtist),
      "social worker" -> instanceOrOccupation(Q.socialWorker),
      "Nordic combined skier" -> instanceOrOccupation(Q.nordicCombinedSkier),
      "equestrian" -> instanceOrOccupation(Q.equestrian),
      "civil law notary" -> instanceOrOccupation(Q.civilLawNotary),
      "freestyle skier" -> instanceOrOccupation(Q.freestyleSkier),
      "pharmacologist" -> instanceOrOccupation(Q.pharmacologist),
      "darts player" -> instanceOrOccupation(Q.dartsPlayer),
      "karateka" -> instanceOrOccupation(Q.karateka),
      "banjoist" -> instanceOrOccupation(Q.banjoist),
      "drug trafficker" -> instanceOrOccupation(Q.drugTrafficker),
      "physiologist" -> instanceOrOccupation(Q.physiologist),
      "bobsledder" -> instanceOrOccupation(Q.bobsledder),
      "lyricist" -> instanceOrOccupation(Q.lyricist),
      "neurologist" -> instanceOrOccupation(Q.neurologist),
      "anatomist" -> instanceOrOccupation(Q.anatomist),
      "costume designer" -> instanceOrOccupation(Q.costumeDesigner),
      "film critic" -> instanceOrOccupation(Q.filmCritic),
      "submariner" -> instanceOrOccupation(Q.submariner),
      "stage actor" -> instanceOrOccupation(Q.stageActor),
      "restaurateur" -> instanceOrOccupation(Q.restaurateur),
      "cellist" -> instanceOrOccupation(Q.cellist),
      "pathologist" -> instanceOrOccupation(Q.pathologist),
      "educator" -> instanceOrOccupation(Q.educator),
      "egyptologist" -> instanceOrOccupation(Q.egyptologist),
      "ethnologist" -> instanceOrOccupation(Q.ethnologist),
      "bridge player" -> instanceOrOccupation(Q.bridgePlayer),
      "programmer" -> instanceOrOccupation(Q.programmer),
      "astrophysicist" -> instanceOrOccupation(Q.astrophysicist),
      "performance artist" -> instanceOrOccupation(Q.performanceArtist),
      "test pilot" -> instanceOrOccupation(Q.testPilot),
      "fiddler" -> instanceOrOccupation(Q.fiddler),
      "art critic" -> instanceOrOccupation(Q.artCritic),
      "restorer" -> instanceOrOccupation(Q.restorer),
      "religious servant" -> instanceOrOccupation(Q.religiousServant),
      "docent" -> instanceOrOccupation(Q.docent),
      "triathlete" -> instanceOrOccupation(Q.triathlete),
      "rally driver" -> instanceOrOccupation(Q.rallyDriver),
      "spy" -> instanceOrOccupation(Q.spy),
      "activist" -> instanceOrOccupation(Q.activist),
      "blogger" -> instanceOrOccupation(Q.blogger),
      "military historian" -> instanceOrOccupation(Q.militaryHistorian),
      "salon-holder" -> instanceOrOccupation(Q.salonHolder),
      "gynaecologist" -> instanceOrOccupation(Q.gynaecologist),
      "motivational speaker" -> instanceOrOccupation(Q.motivationalSpeaker),
      "genealogist" -> instanceOrOccupation(Q.genealogist),
      "hornist" -> instanceOrOccupation(Q.hornist),
      "malacologist" -> instanceOrOccupation(Q.malacologist),
      "fighter pilot" -> instanceOrOccupation(Q.fighterPilot),
      "microbiologist" -> instanceOrOccupation(Q.microbiologist),
      "draughtsman" -> instanceOrOccupation(Q.draughtsman),
      "horticulturist" -> instanceOrOccupation(Q.horticulturist),
      "etcher" -> instanceOrOccupation(Q.etcher),
      "printmaker" -> instanceOrOccupation(Q.printmaker),
      "visual artist" -> instanceOrOccupation(Q.visualArtist),
      "carcinologist" -> instanceOrOccupation(Q.carcinologist),
      "music critic" -> instanceOrOccupation(Q.musicCritic),
      "school teacher" -> instanceOrOccupation(Q.schoolTeacher),
      "pole vaulter" -> instanceOrOccupation(Q.poleVaulter),
      "beauty pageant contestant" -> instanceOrOccupation(Q.beautyPageantContestant),
      "music historian" -> instanceOrOccupation(Q.musicHistorian),
      "art director" -> instanceOrOccupation(Q.artDirector),
      "calligrapher" -> instanceOrOccupation(Q.calligrapher),
      "romanist" -> instanceOrOccupation(Q.romanist),
      "vocalist" -> instanceOrOccupation(Q.vocalist),
      "copperplate engraver" -> instanceOrOccupation(Q.copperplateEngraver),
      "orientalist" -> instanceOrOccupation(Q.orientalist),
      "sports journalist" -> instanceOrOccupation(Q.sportsJournalist),
      "academic" -> instanceOrOccupation(Q.academic),
      "oboist" -> instanceOrOccupation(Q.oboist),
      "gardener" -> instanceOrOccupation(Q.gardener),
      "historian of mathematics" -> instanceOrOccupation(Q.historianOfMathematics),
      "flying ace" -> instanceOrOccupation(Q.flyingAce),
      "rancher" -> instanceOrOccupation(Q.rancher),
      "philanthropist" -> instanceOrOccupation(Q.philanthropist),
      "researcher" -> instanceOrOccupation(Q.researcher),
      "numismatist" -> instanceOrOccupation(Q.numismatist),
      "puppeteer" -> instanceOrOccupation(Q.puppeteer),
      "surfer" -> instanceOrOccupation(Q.surfer),
      "cyclo-cross cyclist" -> instanceOrOccupation(Q.cycloCrossCyclist),
      "landscape architect" -> instanceOrOccupation(Q.landscapeArchitect),
      "urban planner" -> instanceOrOccupation(Q.urbanPlanner),
      "molecular biologist" -> instanceOrOccupation(Q.molecularBiologist),
      "reporter" -> instanceOrOccupation(Q.reporter),
      "polo player" -> instanceOrOccupation(Q.poloPlayer),
      "goldsmith" -> instanceOrOccupation(Q.goldsmith),
      "newspaper editor" -> instanceOrOccupation(Q.newspaperEditor),
      "chief executive officer" -> instanceOrOccupation(Q.chiefExecutiveOfficer),
      "nuclear scientist" -> instanceOrOccupation(Q.nuclearScientist),
      "preacher" -> instanceOrOccupation(Q.preacher),
      "cardiologist" -> instanceOrOccupation(Q.cardiologist),
      "lobbyist" -> instanceOrOccupation(Q.lobbyist),
      "club DJ" -> instanceOrOccupation(Q.clubDj),
      "stockbroker" -> instanceOrOccupation(Q.stockbroker),
      "netballer" -> instanceOrOccupation(Q.netballer),
      "chess composer" -> instanceOrOccupation(Q.chessComposer),
      "character actor" -> instanceOrOccupation(Q.characterActor),
      "immunologist" -> instanceOrOccupation(Q.immunologist),
      "illuminator" -> instanceOrOccupation(Q.illuminator),
      "referee" -> instanceOrOccupation(Q.referee),
      "agronomist" -> instanceOrOccupation(Q.agronomist),
      "Playboy Playmate" -> instanceOrOccupation(Q.playboyPlaymate),
      "YouTuber" -> instanceOrOccupation(Q.youtuber),
      "American football player" -> instanceOrOccupation(Q.americanFootballPlayer),
      "king" -> instanceOrOccupation(Q.king),
      "lecturer" -> instanceOrOccupation(Q.lecturer),
      "troubadour" -> instanceOrOccupation(Q.troubadour),
      "ambassador" -> instanceOrOccupation(Q.ambassador),
      "criminologist" -> instanceOrOccupation(Q.criminologist),
      "bookseller" -> instanceOrOccupation(Q.bookseller),
      "pediatrician" -> instanceOrOccupation(Q.pediatrician),
      "luth player" -> instanceOrOccupation(Q.luthPlayer),
      "ultramarathon runner" -> instanceOrOccupation(Q.ultramarathonRunner),
      "bartender" -> instanceOrOccupation(Q.bartender),
      "astrologer" -> instanceOrOccupation(Q.astrologer),
      "street artist" -> instanceOrOccupation(Q.streetArtist),
      "hammer thrower" -> instanceOrOccupation(Q.hammerThrower),
      "theatre critic" -> instanceOrOccupation(Q.theatreCritic),
      "sports commentator" -> instanceOrOccupation(Q.sportsCommentator),
      "bacteriologist" -> instanceOrOccupation(Q.bacteriologist),
      "epidemiologist" -> instanceOrOccupation(Q.epidemiologist),
      "manufacturer" -> instanceOrOccupation(Q.manufacturer),
      "builder" -> instanceOrOccupation(Q.builder),
      "war correspondent" -> instanceOrOccupation(Q.warCorrespondent),
      "statesman" -> instanceOrOccupation(Q.statesman),
      "make-up artist" -> instanceOrOccupation(Q.makeUpArtist),
      "architectural historian" -> instanceOrOccupation(Q.architecturalHistorian),
      "luthier" -> instanceOrOccupation(Q.luthier),
      "mandolinist" -> instanceOrOccupation(Q.mandolinist),
      "parson" -> instanceOrOccupation(Q.parson),
      "botanical illustrator" -> instanceOrOccupation(Q.botanicalIllustrator),
      "dermatologist" -> instanceOrOccupation(Q.dermatologist),
      "nun" -> instanceOrOccupation(Q.nun),
      "oncologist" -> instanceOrOccupation(Q.oncologist),
      "dramaturge" -> instanceOrOccupation(Q.dramaturge),
      "henchman" -> instanceOrOccupation(Q.henchman),
      "economic historian" -> instanceOrOccupation(Q.economicHistorian),
      "NASCAR team owner" -> instanceOrOccupation(Q.nascarTeamOwner),
      "Lady-in-waiting" -> instanceOrOccupation(Q.ladyInWaiting),
      "mining engineer" -> instanceOrOccupation(Q.miningEngineer),
      "psychoanalyst" -> instanceOrOccupation(Q.psychoanalyst),
      "ethnomusicologist" -> instanceOrOccupation(Q.ethnomusicologist),
      "trade unionist" -> instanceOrOccupation(Q.tradeUnionist),
      "chamberlain" -> instanceOrOccupation(Q.chamberlain),
      "luger" -> instanceOrOccupation(Q.luger),
      "skeleton racer" -> instanceOrOccupation(Q.skeletonRacer),
      "topologist" -> instanceOrOccupation(Q.topologist),
      "academician" -> instanceOrOccupation(Q.academician),
      "winemaker" -> instanceOrOccupation(Q.winemaker),
      "figure skater" -> instanceOrOccupation(Q.figureSkater),
      "radio DJ" -> instanceOrOccupation(Q.radioDj),
      "player of Basque pelota" -> instanceOrOccupation(Q.playerOfBasquePelota),
      "medical historian" -> instanceOrOccupation(Q.medicalHistorian),
      "church musician" -> instanceOrOccupation(Q.churchMusician),
      "watchmaker" -> instanceOrOccupation(Q.watchmaker),
      "bowler" -> instanceOrOccupation(Q.bowler),
      "matador" -> instanceOrOccupation(Q.matador),
      "typographer" -> instanceOrOccupation(Q.typographer),
      "accountant" -> instanceOrOccupation(Q.accountant),
      "diver" -> instanceOrOccupation(Q.diver),
      "child actor" -> instanceOrOccupation(Q.childActor),
      "handball coach" -> instanceOrOccupation(Q.handballCoach),
      "cryptographer" -> instanceOrOccupation(Q.cryptographer),
      "music executive" -> instanceOrOccupation(Q.musicExecutive),
      "canoer" -> instanceOrOccupation(Q.canoer),
      "literary editor" -> instanceOrOccupation(Q.literaryEditor),
      "bryologist" -> instanceOrOccupation(Q.bryologist),
      "obstetrician" -> instanceOrOccupation(Q.obstetrician),
      "gymnast" -> instanceOrOccupation(Q.gymnast),
      "mechanical engineer" -> instanceOrOccupation(Q.mechanicalEngineer),
      "baseball umpire" -> instanceOrOccupation(Q.baseballUmpire),
      "Catholic bishop" -> instanceOrOccupation(Q.catholicBishop),
      "affichiste" -> instanceOrOccupation(Q.affichiste),
      "faculty" -> instanceOrOccupation(Q.faculty),
      "nutritionist" -> instanceOrOccupation(Q.nutritionist),
      "medallist" -> instanceOrOccupation(Q.medallist),
      "hurdler" -> instanceOrOccupation(Q.hurdler),
      "neurosurgeon" -> instanceOrOccupation(Q.neurosurgeon),
      "pool player" -> instanceOrOccupation(Q.poolPlayer),
      "war photographer" -> instanceOrOccupation(Q.warPhotographer),
      "mechanic" -> instanceOrOccupation(Q.mechanic),
      "corvette captain" -> instanceOrOccupation(Q.corvetteCaptain),
      "revolutionary" -> instanceOrOccupation(Q.revolutionary),
      "speleologist" -> instanceOrOccupation(Q.speleologist),
      "navigator" -> instanceOrOccupation(Q.navigator),
      "short story writer" -> instanceOrOccupation(Q.shortStoryWriter),
      "art dealer" -> instanceOrOccupation(Q.artDealer),
      "midwife" -> instanceOrOccupation(Q.midwife),
      "aristocrat" -> instanceOrOccupation(Q.aristocrat),
      "ostracodologist" -> instanceOrOccupation(Q.ostracodologist),
      "rector" -> instanceOrOccupation(Q.rector),
      "acarologist" -> instanceOrOccupation(Q.acarologist),
      "religious" -> instanceOrOccupation(Q.religious),
      "literary" -> instanceOrOccupation(Q.literary),
      "textile artist" -> instanceOrOccupation(Q.textileArtist),
      "torturer" -> instanceOrOccupation(Q.torturer),
      "chansonnier" -> instanceOrOccupation(Q.chansonnier),
      "ufologist" -> instanceOrOccupation(Q.ufologist),
      "mammalogist" -> instanceOrOccupation(Q.mammalogist),
      "geodesist" -> instanceOrOccupation(Q.geodesist),
      "pteridologist" -> instanceOrOccupation(Q.pteridologist),
      "biographer" -> instanceOrOccupation(Q.biographer),
      "stunt performer" -> instanceOrOccupation(Q.stuntPerformer),
      "seismologist" -> instanceOrOccupation(Q.seismologist),
      "secretary" -> instanceOrOccupation(Q.secretary),
      "filmmaker" -> instanceOrOccupation(Q.filmmaker),
      "muhaddith" -> instanceOrOccupation(Q.muhaddith),
      "industrialist" -> instanceOrOccupation(Q.industrialist),
      "mountain guide" -> instanceOrOccupation(Q.mountainGuide),
      "deacon" -> instanceOrOccupation(Q.deacon),
      "skipper" -> instanceOrOccupation(Q.skipper),
      "wood carver" -> instanceOrOccupation(Q.woodCarver),
      "music director" -> instanceOrOccupation(Q.musicDirector),
      "consultant" -> instanceOrOccupation(Q.consultant),
      "jurist-consultant" -> instanceOrOccupation(Q.juristConsultant),
      "shipowner" -> instanceOrOccupation(Q.shipowner),
      "private investigator" -> instanceOrOccupation(Q.privateInvestigator),
      "high jumper" -> instanceOrOccupation(Q.highJumper),
      "long jumper" -> instanceOrOccupation(Q.longJumper),
      "iconographer" -> instanceOrOccupation(Q.iconographer),
      "virologist" -> instanceOrOccupation(Q.virologist),
      "intendant" -> instanceOrOccupation(Q.intendant),
      "bibliographer" -> instanceOrOccupation(Q.bibliographer),
      "impresario" -> instanceOrOccupation(Q.impresario),
      "functionary" -> instanceOrOccupation(Q.functionary),
      "editor-in-chief" -> instanceOrOccupation(Q.editorInChief),
      "radio producer" -> instanceOrOccupation(Q.radioProducer),
      "motocross rider" -> instanceOrOccupation(Q.motocrossRider),
      "assessor" -> instanceOrOccupation(Q.assessor),
      "Geheimrat" -> instanceOrOccupation(Q.geheimrat),
      "geopolitician" -> instanceOrOccupation(Q.geopolitician),
      "chronicler" -> instanceOrOccupation(Q.chronicler),
      "Anglican priest" -> instanceOrOccupation(Q.anglicanPriest),
      "solicitor" -> instanceOrOccupation(Q.solicitor),
      "director of church music" -> instanceOrOccupation(Q.directorOfChurchMusic),
      "cultural historian" -> instanceOrOccupation(Q.culturalHistorian),
      "video game developer" -> instanceOrOccupation(Q.videoGameDeveloper),
      "Akhoond" -> instanceOrOccupation(Q.akhoond),
      "sound artist" -> instanceOrOccupation(Q.soundArtist),
      "trombonist" -> instanceOrOccupation(Q.trombonist),
      "theoretical physicist" -> instanceOrOccupation(Q.theoreticalPhysicist),
      "sinologist" -> instanceOrOccupation(Q.sinologist),
      "prosecutor" -> instanceOrOccupation(Q.prosecutor),
      "talent agent" -> instanceOrOccupation(Q.talentAgent),
      "kickboxer" -> instanceOrOccupation(Q.kickboxer),
      "rugby player" -> instanceOrOccupation(Q.rugbyPlayer),
      "hagiographer" -> instanceOrOccupation(Q.hagiographer),
      "beekeeper" -> instanceOrOccupation(Q.beekeeper),
      "chaplain" -> instanceOrOccupation(Q.chaplain),
      "oceanographer" -> instanceOrOccupation(Q.oceanographer),
      "curate" -> instanceOrOccupation(Q.curate),
      "bioinformatician" -> instanceOrOccupation(Q.bioinformatician),
      "balloonist" -> instanceOrOccupation(Q.balloonist),
      "program maker" -> instanceOrOccupation(Q.programMaker),
      "resistance fighter" -> instanceOrOccupation(Q.resistanceFighter),
      "music arranger" -> instanceOrOccupation(Q.musicArranger),
      "business manager" -> instanceOrOccupation(Q.businessManager),
      "Rowing coach" -> instanceOrOccupation(Q.rowingCoach),
      "Liedermacher" -> instanceOrOccupation(Q.liedermacher),
      "draughts player" -> instanceOrOccupation(Q.draughtsPlayer),
      "provost" -> instanceOrOccupation(Q.provost),
      "joiner" -> instanceOrOccupation(Q.joiner),
      "prelate" -> instanceOrOccupation(Q.prelate),
      "business executive" -> instanceOrOccupation(Q.businessExecutive),
      "electrical engineer" -> instanceOrOccupation(Q.electricalEngineer),
      "faqih" -> instanceOrOccupation(Q.faqih),
      "stained-glass artist" -> instanceOrOccupation(Q.stainedGlassArtist),
      "social scientist" -> instanceOrOccupation(Q.socialScientist),
      "ice hockey coach" -> instanceOrOccupation(Q.iceHockeyCoach),
      "patron" -> instanceOrOccupation(Q.patron),
      "miner" -> instanceOrOccupation(Q.miner),
      "parasitologist" -> instanceOrOccupation(Q.parasitologist),
      "privateer" -> instanceOrOccupation(Q.privateer),
      "military leader" -> instanceOrOccupation(Q.militaryLeader),
      "lighting designer" -> instanceOrOccupation(Q.lightingDesigner),
      "music producer" -> instanceOrOccupation(Q.musicProducer),
      "discus thrower" -> instanceOrOccupation(Q.discusThrower),
      "medical writer" -> instanceOrOccupation(Q.medicalWriter),
      "con artist" -> instanceOrOccupation(Q.conArtist),
      "historian of religion" -> instanceOrOccupation(Q.historianOfReligion),
      "torero" -> instanceOrOccupation(Q.torero),
      "harpsichordist" -> instanceOrOccupation(Q.harpsichordist),
      "advocate" -> instanceOrOccupation(Q.advocate)
    )

  val adjectiveFactories: mutable.Map[String, NodeFactory] =
    mutable.Map("most" -> {
      (node, env) =>
        node.aggregate(Count).order(Descending)
    })

  def wrapAdjective(adjectives: Seq[Token], node: WikidataNode,
                    env: WikidataEnvironment): WikidataNode =
    if (adjectives.isEmpty) node
    else {
      val adjectiveWords = mkWordString(adjectives)
      adjectiveFactories.get(adjectiveWords)
          .map(_(node, env))
          .getOrElse(node)
    }

}


trait ValueNodeFactory {

  def makeValueNode(name: Seq[Token], filter: Seq[Token],
                    env: WikidataEnvironment): WikidataNode =
  {
    import ValueNodeFactory._

    val (adjectives, nameRest) = splitName(name)
    val lemmatized = mkLemmaString(nameRest)

    val node = env.newNode()
    factories.get(lemmatized) map { factory =>
      val result = factory(node, env)
      makeAdjectiveEdge(adjectives, lemmatized, env) map {
        result.and
      } getOrElse {
        wrapAdjective(adjectives, result, env)
      }
    } getOrElse {
      val words = mkWordString(name)
      node.out(NameLabel, words)
    }
  }

}
