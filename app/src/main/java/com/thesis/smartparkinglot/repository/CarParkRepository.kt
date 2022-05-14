package com.thesis.smartparkinglot.repository

import com.thesis.smartparkinglot.retrofit.APIService
import com.thesis.smartparkinglot.room.UserRoomDatabase
import com.thesis.smartparkinglot.Result
import com.thesis.smartparkinglot.retrofit.response.UserResponse
import com.thesis.smartparkinglot.room.mapper.UserMapper

class CarParkRepository(var room: UserRoomDatabase, private var apiService: APIService) {

    private suspend fun updateUser(userResponse: UserResponse?) {
        userResponse?.let {
            room.userDao().deleteAll()
            room.userDao().insert(UserMapper.responseToEntity(userResponse))

        }
    }

     suspend fun getUserInfo(userId: String) : Result<UserResponse> {
        return try {
            val response = apiService.showDB("user", userId.substring(1, userId.length - 1))

            if (response.isSuccessful) {
                if(response.body() != null) {
                    updateUser(userResponse = response.body() as UserResponse)
                    Result.Success(response.body(), "Successful")
                } else {
                    Result.Error(java.lang.Exception("Something went wrong!"))
                }
            } else {
//                var user = room.userDao().getUserById(userId)
                Result.Success(response.body(), "Cannot fetch user from network, get from ROOM")
//                Result.Error(java.lang.Exception("Something went wrong!"))
            }
        }catch (exception : Exception){
            Result.Error(exception)
        }
    }
}