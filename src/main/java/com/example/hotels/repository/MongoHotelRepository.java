package com.example.hotels.repository;

import com.example.hotels.entity.Address;
import com.example.hotels.entity.ArrivalTime;
import com.example.hotels.entity.Contacts;
import com.example.hotels.entity.Hotel;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * MongoDB implementation of IHotelRepository.
 * Active only when the 'mongodb' profile is enabled.
 * Uses the MongoDB Java Driver directly (no Spring Data MongoDB).
 */
@Component
@Profile("mongodb")
public class MongoHotelRepository implements IHotelRepository {

    private static final String COLLECTION = "hotels";
    private final MongoDatabase database;

    public MongoHotelRepository(MongoDatabase database) {
        this.database = database;
    }

    private MongoCollection<Document> collection() {
        return database.getCollection(COLLECTION);
    }

    @Override
    public List<Hotel> findAll() {
        List<Hotel> hotels = new ArrayList<>();
        for (Document doc : collection().find()) {
            hotels.add(toHotel(doc));
        }
        return hotels;
    }

    @Override
    public Optional<Hotel> findByIdWithAmenities(Long id) {
        Document doc = collection().find(Filters.eq("_id", id)).first();
        return Optional.ofNullable(doc != null ? toHotel(doc) : null);
    }

    @Override
    public List<Hotel> searchByFilters(String name, String brand, String city, String country) {
        List<Bson> filters = new ArrayList<>();

        if (name != null) {
            filters.add(Filters.regex("name", Pattern.quote(name), "i"));
        }
        if (brand != null) {
            filters.add(Filters.regex("brand", Pattern.quote(brand), "i"));
        }
        if (city != null) {
            filters.add(Filters.regex("address.city", Pattern.quote(city), "i"));
        }
        if (country != null) {
            filters.add(Filters.regex("address.country", Pattern.quote(country), "i"));
        }

        Bson query = filters.isEmpty() ? new Document() : Filters.and(filters);

        List<Hotel> hotels = new ArrayList<>();
        for (Document doc : collection().find(query)) {
            hotels.add(toHotel(doc));
        }
        return hotels;
    }

    @Override
    public Hotel save(Hotel hotel) {
        Document doc = toDocument(hotel);
        if (hotel.getId() != null) {
            collection().replaceOne(Filters.eq("_id", hotel.getId()), doc, new ReplaceOptions().upsert(true));
        } else {
            InsertOneResult result = collection().insertOne(doc);
            if (result.getInsertedId() != null) {
                hotel.setId(result.getInsertedId().asInt64().getValue());
            }
        }
        return hotel;
    }

    // ---- Mapping helpers ----

    private Document toDocument(Hotel hotel) {
        Document doc = new Document();
        if (hotel.getId() != null) {
            doc.put("_id", hotel.getId());
        }
        doc.put("name", hotel.getName());
        doc.put("description", hotel.getDescription());
        doc.put("brand", hotel.getBrand());

        if (hotel.getAddress() != null) {
            Address a = hotel.getAddress();
            doc.put("address", new Document()
                    .append("houseNumber", a.getHouseNumber())
                    .append("street", a.getStreet())
                    .append("city", a.getCity())
                    .append("country", a.getCountry())
                    .append("postCode", a.getPostCode()));
        }

        if (hotel.getContacts() != null) {
            Contacts c = hotel.getContacts();
            doc.put("contacts", new Document()
                    .append("phone", c.getPhone())
                    .append("email", c.getEmail()));
        }

        if (hotel.getArrivalTime() != null) {
            ArrivalTime at = hotel.getArrivalTime();
            doc.put("arrivalTime", new Document()
                    .append("checkIn", at.getCheckIn())
                    .append("checkOut", at.getCheckOut()));
        }

        doc.put("amenities", hotel.getAmenities() != null ? new ArrayList<>(hotel.getAmenities()) : new ArrayList<>());
        return doc;
    }

    @SuppressWarnings("unchecked")
    private Hotel toHotel(Document doc) {
        Hotel hotel = new Hotel();
        Object id = doc.get("_id");
        if (id instanceof Number) {
            hotel.setId(((Number) id).longValue());
        } else if (id instanceof ObjectId oid) {
            hotel.setId(oid.hashCode() & 0xFFFFFFFFL);
        }
        hotel.setName(doc.getString("name"));
        hotel.setDescription(doc.getString("description"));
        hotel.setBrand(doc.getString("brand"));

        Document addrDoc = (Document) doc.get("address");
        if (addrDoc != null) {
            hotel.setAddress(new Address(
                    addrDoc.getInteger("houseNumber"),
                    addrDoc.getString("street"),
                    addrDoc.getString("city"),
                    addrDoc.getString("country"),
                    addrDoc.getString("postCode")));
        }

        Document contactsDoc = (Document) doc.get("contacts");
        if (contactsDoc != null) {
            hotel.setContacts(new Contacts(
                    contactsDoc.getString("phone"),
                    contactsDoc.getString("email")));
        }

        Document arrivalDoc = (Document) doc.get("arrivalTime");
        if (arrivalDoc != null) {
            hotel.setArrivalTime(new ArrivalTime(
                    arrivalDoc.getString("checkIn"),
                    arrivalDoc.getString("checkOut")));
        }

        List<String> amenities = (List<String>) doc.get("amenities");
        hotel.setAmenities(amenities != null ? new ArrayList<>(amenities) : new ArrayList<>());

        return hotel;
    }
}
