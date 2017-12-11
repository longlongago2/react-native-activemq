##react-native-activemq

### 安装
`npm install react-native-activemq --save`

### 使用
`import { ActiveMQ } from 'react-native-activemq'`

**连接服务**
`
const connectConfig = {
    ip: string,
};
`
`ActiveMQ.connect(connectConfig:object);`

**关闭服务**
`ActiveMQ.close();`

**订阅服务**
`ActiveMQ.subscribe();`