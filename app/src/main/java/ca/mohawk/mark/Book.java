package ca.mohawk.mark;

import android.graphics.Bitmap;

/**
 * Book Class
 *
 * Represents a Book object retrieved from the Google Books API.
 */
public class Book {
    private String title;
    private String description;
    private int pages;
    private String publisher;
    private String publishedDate;
    private String imageURL;
    private Bitmap thumbnail;

    /**
     * Book Constructor
     *
     * @param title book title
     * @param description book description
     * @param pages number of book pages
     * @param publishedDate book publication date
     * @param publisher book publisher
     * @param imageURL book cover image URL
     * @param thumbnail book cover thumbnail
     */
    public Book(String title, String description, int pages, String publishedDate, String publisher, String imageURL, Bitmap thumbnail) {
        this.title = title;
        this.description = description;
        this.pages = pages;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.imageURL = imageURL;
        this.thumbnail = thumbnail;
    }

    /**
     * Retrieves the book's title.
     * @return book title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the book's description.
     * @return book description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retrieves the book's number of pages.
     * @return number of book pages
     */
    public int getPages() {
        return pages;
    }

    /**
     * Retrieves the book's published date.
     * @return book publication date
     */
    public String getPublishedDate() {
        return publishedDate;
    }

    /**
     * Retrieves the book's publisher.
     * @return book publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Retrieves the book's thumbnail bitmap.
     * @return book thumbnail
     */
    public Bitmap getThumbnail() { return thumbnail; }
}
