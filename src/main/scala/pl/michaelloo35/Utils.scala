package pl.michaelloo35

// REQUESTS
sealed trait BookRequest

case class SearchRequest(title: String) extends BookRequest

case class OrderRequest(title: String) extends BookRequest

case class StreamRequest(title: String) extends BookRequest

// RESPONSES
sealed trait BookResponse

case class OrderResponse(message: String) extends BookResponse

case class StreamFailure(reason: String) extends BookResponse

case class StreamReply(line: String) extends BookResponse

case class SearchSuccess(title: String, price: Double) extends BookResponse

case class SearchFailure(title: String, reason: String) extends BookResponse

// NEW TYPE

sealed abstract class State

case object NotReplied extends State
case object Waiting extends State
case object Replied extends State