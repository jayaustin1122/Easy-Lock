package com.example.easylock.model

class LogsModel {

    var fullName: String = ""
    var image: String = ""
    var currentDate: String = ""
    var currentTime: String = ""
    var status: Boolean = true
    var id: String = ""

    constructor(
        fullName : String,
        image : String,
        currentDate : String,
        currentTime : String,
        status : Boolean,
        id : String
    ){

        this.fullName = fullName

        this.image = image
        this.currentDate = currentDate
        this.currentTime = currentTime
        this.status = status
        this.id = id
    }
    constructor()
}