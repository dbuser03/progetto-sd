package it.unimib.sd2024;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe risorsa per la gestione degli ordini di domini.
 * Utilizza JAX-RS per esporre API RESTful.
 */
@Path("orders")
public class UserOrderRequestResource {
  // Indirizzo e porta del database, per la connessione via socket
  private static final String DATABASE_HOST = "localhost";
  private static final int DATABASE_PORT = 3030;
  // Istanza di Jsonb per la serializzazione e deserializzazione JSON
  private final Jsonb jsonb = JsonbBuilder.create();

  /**
   * Stabilisce una connessione con il database e gestisce la risposta.
   *
   * @param command Il comando da inviare al database.
   * @return Un oggetto Response contenente il risultato dell'operazione.
   */
  private Response connectToDatabaseAndHandleResponse(String command) {
    try (Socket socket = new Socket(DATABASE_HOST, DATABASE_PORT);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      // Invia il comando al database
      out.println(command);
      // Legge la risposta
      String response = in.readLine();
      // Gestisce il caso di risposta nulla
      if (response == null) {
        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Database connection error").build();
      }
      // Restituisce la risposta in caso di successo
      return Response.ok(response).build();
    } catch (IOException e) {
      // Gestisce eventuali errori di connessione
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Database connection error").build();
    }
  }

  /**
   * Recupera tutti gli ordini dal database.
   *
   * @param userId ID dell'utente per cui filtrare gli ordini, opzionale.
   * @return Un oggetto Response contenente tutti gli ordini.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getOrders(@QueryParam("userId") String userId) {
    String command;
    // Costruisce il comando in base alla presenza dell'userId
    if (userId != null && !userId.isEmpty()) {
      command = "GET orders" + userId;
    } else {
      command = "GET orders";
    }
    // Connette al database e restituisce la risposta
    return connectToDatabaseAndHandleResponse(command);
  }

  /**
   * Elabora una richiesta per creare un nuovo ordine.
   *
   * @param orderRequest La richiesta contenente le informazioni dell'ordine.
   * @return Un oggetto Response che indica l'esito del tentativo di creazione.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createOrder(UserOrderRequest orderRequest) {
    // Crea un oggetto JSON con le informazioni dell'ordine
    JsonObject orderJson = Json.createObjectBuilder()
        .add("userId", orderRequest.getUserId())
        .add("domainId", orderRequest.getDomainId())
        .add("orderDate", orderRequest.getOrderDate())
        .add("type", orderRequest.getType())
        .add("price", orderRequest.getPrice())
        .build();

    // Costruisce il comando per inserire l'ordine nel database
    String command = "POST orders" + jsonb.toJson(orderJson);

    // Connette al database e restituisce la risposta
    return connectToDatabaseAndHandleResponse(command);
  }
}