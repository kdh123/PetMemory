package com.dohyun.petmemory.ui.profile.edit

interface ProfileEditSideEffect {
    object None : ProfileEditSideEffect
    data class Message(val message: String) : ProfileEditSideEffect
}