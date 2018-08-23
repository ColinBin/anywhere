# Now @[anywhere-android](https://github.com/ColinBin/anywhere-android), @[anywhere-flask](https://github.com/ColinBin/anywhere-flask)

# About
This application is based on LBS. The **main idea** is to allow users to place posts in specific locations-i.e, where they are, and posts could be seen only when users are nearby the location of posts. This idea is inspired by **Pokemon go**.

# Project
- App is built and debugged on Android Studio and run on Android devices with sdk version over 17. Net operations is supported with [Nohttp](https://github.com/yanzhenjie/NoHttp) and location-based service is required.
- Server is built and debugged with python 3.4.4 on CentOS 7. Built with Flask and SQLAlchemy, the server deals with coming requests and operates data in MySql database.

# Idea
- After enabling LBS, users can fetch posts according to his/her current location.
- User can "place" posts where they are, which could be revealed to others nearby.
- A post is basically composed of a title and some lines. But author could lock the post with a password which could be shared with others so that they can check the post. A post also comes with an optional picture, which could be picked from album or taken with camera.
- A post can be commented and liked or disliked.

# Characteristics
- Support auto-login.
- Both album and camera can be sources of avatars and post pictures.
- Support both English and Chinese. Languages could be switched in settings. Effect after reboot.
- User's password is encrypted with MD5 and transmitted to server for safety.
- When requesting location descriptions, users under English settings get response from Google whereas users under Chinese settings get response from Baidu. 

> Baidu map api does not support English description but when dealing with Chinese locations, Baidu map comes with more specific descriptions than Google map. 

# TODO
1. UI.
2. Better compatibility between langugaes.
3. Better login stabillity.
4. Add styles to posts.
5. Remedy on server failure.
6. **Expand this idea.**
