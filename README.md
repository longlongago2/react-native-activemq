##react-native-activemq

### 安装
`npm install react-native-activemq --save`

### 使用
`import { ActiveMQ } from 'react-native-activemq'`

**连接服务**
`ActiveMQ.connect( topicName : string, userid : string);`

**关闭服务**
`ActiveMQ.disconnect();`