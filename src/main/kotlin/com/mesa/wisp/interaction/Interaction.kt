package com.mesa.wisp.interaction

enum class Interaction(val verbPresent: String, val verbPast: String, val plural: String) {
    POKE("poke","poked", "pokes"),
    HANDSHAKE("shake hands with", "shook hands with", "handshakes"),
    HIGHFIVE("high-five", "high-fived", "high-fives"),
    HUG("hug", "hugged", "hugs"),
    CUDDLE("cuddle", "cuddled", "cuddles"),
    PET("pet","petted", "pets"),
    KISS("kiss","kissed", "kisses"),
    LICK("lick", "licked", "licks"),
    SLAP("slap","slapped", "slaps")
}
