package cn.edu360.kafka;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class ProducerDemo {
	public static void main(String[] args) {
		Properties props = new Properties();
		/**指定kafka集群*/
		props.put("metadata.broker.list", "172.17.1.247:9092");
		/**指定序列化器，默认使用StringEncoder*/
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		ProducerConfig config = new ProducerConfig(props);
		Producer<String, String> producer = new Producer<String, String>(config);
		for (int i = 1001; i <= 1100; i++)
			/**producer向kafka发送KeyedMessage，该对象里面封装 topic和message*/
			producer.send(new KeyedMessage<String, String>("gy", "msg" + i));
	}
}