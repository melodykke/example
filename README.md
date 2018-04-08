# dubbo集成zookeeper集群

用于原生zookeeper api没有节点级联的CDRWA，以及不方便在父节点上添加watcher以监听
子节点们，所以不方便使用。
遂对zookeeper的操作使用Curator。
完成分布式锁，以同步（排队）处理请求。