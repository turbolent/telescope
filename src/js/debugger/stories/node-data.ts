export default {
    "$type": "com.turbolent.questionCompiler.graph.Node",
    "aggregates": [],
    "edge": {
        "$type": "com.turbolent.questionCompiler.graph.ConjunctionEdge",
        "edges": [
            {
                "$type": "com.turbolent.questionCompiler.graph.OutEdge",
                "label": {
                    "$type": "com.turbolent.wikidataOntology.PropertyLabel",
                    "property": {
                        "$type": "com.turbolent.wikidataOntology.Property",
                        "id": 39,
                        "name": "holds position"
                    }
                },
                "target": {
                    "$type": "com.turbolent.questionCompiler.graph.Node",
                    "aggregates": [],
                    "label": {
                        "$type": "com.turbolent.wikidataOntology.ItemLabel",
                        "item": {
                            "$type": "com.turbolent.wikidataOntology.Item",
                            "id": 30461,
                            "name": "president"
                        }
                    }
                }
            },
            {
                "$type": "com.turbolent.questionCompiler.graph.OutEdge",
                "label": {
                    "$type": "com.turbolent.wikidataOntology.PropertyLabel",
                    "property": {
                        "$type": "com.turbolent.wikidataOntology.Property",
                        "id": 569,
                        "name": "has date of birth"
                    }
                },
                "target": {
                    "$type": "com.turbolent.questionCompiler.graph.Node",
                    "aggregates": [],
                    "filter": {
                        "$type": "com.turbolent.questionCompiler.graph.LessThanFilter",
                        "node": {
                            "$type": "com.turbolent.questionCompiler.graph.Node",
                            "aggregates": [],
                            "label": {
                                "$type": "com.turbolent.wikidataOntology.TemporalLabel",
                                "temporal": {
                                    "$type": "java.time.Year"
                                }
                            }
                        }
                    },
                    "label": {
                        "$type": "com.turbolent.wikidataOntology.VarLabel",
                        "id": 2
                    }
                }
            }
        ]
    },
    "label": {
        "$type": "com.turbolent.wikidataOntology.VarLabel",
        "id": 1
    }
}
