package com.example.easylock.model

class LogsModel {

        var RFID : String = ""
        var date: String = ""
        var time: String = ""


        constructor(
            RFID : String,
            date : String,
            time : String,
        ){

            this.RFID = RFID
            this.date = date
            this.time = time
        }
        constructor()

}