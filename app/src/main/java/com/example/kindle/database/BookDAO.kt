package com.example.kindle.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDAO {
    @Insert
    fun insertBook(bookEntity: BookEntity)


    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * from books")
    fun getAllBooks():List<BookEntity>

    @Query("SELECT * from books WHERE book_id =:bookId")
    fun getBookById(bookId : String):BookEntity

    @Query("DELETE FROM books WHERE book_id =:bookId")
    fun deleteBooks(bookId: String)

}
