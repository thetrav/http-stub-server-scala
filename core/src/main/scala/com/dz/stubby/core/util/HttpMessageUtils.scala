package com.dz.stubby.core.util

import java.util.regex.Matcher
import java.util.regex.Pattern

import com.dz.stubby.core.model.StubMessage

object HttpMessageUtils {

  val ContentTypeHeader = "Content-Type"

  val TextContentType = Pattern.compile("text/.+")
  val JsonContentType = Pattern.compile("application/json(;.+)?")

  def getReasonPhrase(statusCode: Int): String = statusCode match {
    case 200 => "OK"
    case 201 => "Created"
    case 202 => "Accepted"
    case 301 => "Moved Permanently"
    case 302 => "Found"
    case 304 => "Not Modified"
    case 400 => "Bad Request"
    case 401 => "Unauthorized"
    case 403 => "Forbidden"
    case 404 => "Not Found"
    case 406 => "Not Acceptable"
    case 415 => "Unsupported Media Type"
    case 422 => "Unprocessable Entity"
    case 500 => "Internal Server Error"
    case 503 => "Service Unavailable"
    case _ => null
  }

  def upperCaseHeader(name: String): String = {
    val pattern = Pattern.compile("\\-.|^.")
    val matcher = pattern.matcher(name)
    val result = new StringBuffer()
    while (matcher.find()) {
      matcher.appendReplacement(result, matcher.group().toUpperCase())
    }
    matcher.appendTail(result)
    result.toString()
  }

  def isText(m: StubMessage[_]): Boolean =
    m.getHeader(ContentTypeHeader) match {
      case Some(value) => TextContentType.matcher(value).matches
      case _ => false
    }

  def isJson(m: StubMessage[_]): Boolean =
    m.getHeader(ContentTypeHeader) match {
      case Some(value) => JsonContentType.matcher(value).matches
      case _ => false
    }

  def bodyAsText(m: StubMessage[_]): String =
    m.body match {
      case s: String => s
      case _ => throw new RuntimeException("Unexpected body type: " + m.body.getClass)
    }

  def bodyAsJson(m: StubMessage[_]): Any =
    m.body match {
      case s: String => JsonUtils.defaultMapper.readValue(s, classOf[Object]) // support object or array as top-level
      case s: Seq[_] => s // assume already parsed
      case m: scala.collection.Map[_,_] => m // assume already parsed
      case _ => throw new RuntimeException("Unexpected body type: " + m.body.getClass)
    }

}