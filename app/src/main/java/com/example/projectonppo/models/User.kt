package com.example.projectonppo.models

data class User constructor(var name: String = "",
                            var nickname:String = "",
                            var email:String = "",
                            var phone:String = "",
                            var password:String = ""){

    fun compare(b: User?): Boolean{
        if (b == null)
            return false
        if (email != b.email)
            return false
        if (phone != b.phone)
            return false
        if (nickname != b.nickname)
            return false
        if (name != b.name)
            return false
        return true
    }
}
