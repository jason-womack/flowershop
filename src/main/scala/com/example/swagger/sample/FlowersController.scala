package com.example.swagger.sample

import com.example.swagger.sample.data.FlowerData
import com.example.swagger.sample.models.{Flower, Person}
import org.json4s.JsonAST.JValue
import org.scalatra.ScalatraServlet

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

// Swagger support
import org.scalatra.swagger._


class FlowersController(implicit val swagger: Swagger) extends ScalatraServlet  with NativeJsonSupport with SwaggerSupport {

  override protected val applicationName = Some("flowers")
  protected val applicationDescription = "The flowershop API. It exposes operations for browsing and searching lists of flowers, and retrieving single flowers."

  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected override implicit val jsonFormats: Formats = DefaultFormats

  protected override def transformRequestBody(body: JValue): JValue = body.camelizeKeys
  protected override def transformResponseBody(body: JValue): JValue = body.underscoreKeys

  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  /* Retrieve a list of flowers */
  get("/",
    operation((apiOperation[List[Flower]]("getFlowers")
    summary "Show all flowers"
    notes "Shows all the flowers in the flower shop. You can search it too."
    parameter queryParam[Option[String]]("name").description("A name to search for")))) {

    params.get("name") match {
      case Some(name) => FlowerData.all filter (_.name.toLowerCase contains name.toLowerCase)
      case None => FlowerData.all
    }
  }

  get("/:slug",
    operation((apiOperation[Flower]("findBySlug")
    summary "Find by slug"
    parameters (
    pathParam[String]("slug").description("Slug of flower that needs to be fetched")
    )))) {

    FlowerData.all find (_.slug == params("slug")) match {
      case Some(b) => b
      case None => halt(404)
    }
  }

}
