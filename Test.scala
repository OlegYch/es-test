import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.percolator.PercolatorService

trait Common {
  val dataPath = scala.util.Properties.tmpDir + "/es-test"
  val settings = ImmutableSettings.settingsBuilder().
    put("path.data", dataPath).
    put("node.http.enabled", false).
    put("http.enabled", false).
    put("index.number_of_shards", 1).
    put("index.number_of_replicas", 1).
    put("discovery.zen.ping.unicast.hosts", "localhost").
    put("discovery.zen.ping.multicast.enabled", false).
    build()
  val indexName = "test"
  val ids = 1 to 10
  lazy val client = ElasticClient.local(settings)
}
object Step0 extends App with Common {
  import scalax.file._
  Path.fromString(dataPath).deleteRecursively(force = true, continueOnFailure = true)
}
object Step1 extends App with Common {
  for (id <- ids) {
    client.sync.execute(register.id(id.toString).
      into(indexName).
      query(constantScore.filter(idsFilter((1 to 10000).map(id => s"qweqweqwetestsetsetset$id"): _*))))
  }
}
object Step2 extends App with Common {
  client.sync.execute(new ClusterHealth(indexName) {
    override def build: ClusterHealthRequest = super.build.waitForYellowStatus()
  })
  val hits = client.sync.execute(search.in(indexName -> PercolatorService.TYPE_NAME)).getHits.totalHits()
  println(s"hits $hits")
  assert(hits == ids.size)
}
