package controllers;

import com.google.common.io.Files;
import models.Place;
import play.data.Form;
import play.mvc.*;
import views.html.list;
import views.html.placeForm;

import java.io.File;
import java.io.IOException;


public class Application extends Controller {
    private static final Form<Place> addPlaceForm = Form.form(Place.class);

    public static Result list() {
        return ok(list.render(Place.places));
    }

    public static Result addPlace(){
        return ok(placeForm.render(addPlaceForm));
    }

    public static Result showPlace(long id){
        for(Place place : Place.places){
            if(place.id == id){
                return ok(place.picture);
            }
        }
        return badRequest();
    }

    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Form<Place> boundForm = addPlaceForm.bindFromRequest();
        if(boundForm.hasErrors()){
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Please correct the form below:\n");
            flash("error", stringBuffer.toString());
            return badRequest(placeForm.render(boundForm));
        }

        Http.MultipartFormData.FilePart filePart = body.getFile("picture");
        if (filePart == null) {
            flash("error", "Missing file");
            return notFound();
        }

        String contentType = filePart.getContentType();
        File file = filePart.getFile();
        try {
            Place place = new Place();
            place.id = (long) Place.places.size();
            place.picture = Files.toByteArray(file);
            place.name = filePart.getFilename();
            place.contentType = contentType;
            Place.places.add(place);
            return ok(list.render(Place.places));
        } catch(IOException e){
            return internalServerError("Could not save picture");
        }
    }
}