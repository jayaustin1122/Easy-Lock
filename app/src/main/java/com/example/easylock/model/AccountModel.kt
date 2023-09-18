package com.example.easylock.model

class AccountModel {
    var uid: String = ""
    var email: String = ""
    var password: String = ""
    var fullName: String = ""
    var address: String = ""
    var image: String = ""
    var currentDate: String = ""
    var currentTime: String = ""
    var userType: String = ""
    var RFID: String = ""
    var PIN: String = ""
    var status: Boolean = true
    var id: String = ""

    constructor(
        uid :String,
        email : String,
        password : String,
        fullName : String,
        address : String,
        image : String,
        currentDate : String,
        currentTime : String,
        userType : String,
        RFID : String,
        PIN : String,
        status : Boolean,
        id : String
    ){
        this.uid = uid
        this.email = email
        this.password = password
        this.fullName = fullName
        this.address = address
        this.image = image
        this.currentDate = currentDate
        this.currentTime = currentTime
        this.userType = userType
        this.RFID = RFID
        this.PIN = PIN
        this.status = status
        this.id = id
    }
    constructor()


}