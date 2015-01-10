参考这篇[mongodb的文档](http://docs.mongodb.org/manual/tutorial/perform-two-phase-commits/)写了一个小程序测试mongodb的事务操作实现，该程序能执行以下功能：    
1、每次运行程序都从A账户扣100到B账户    
2、可以多个程序同时运行，不会造成账户的金额混乱    
3、金额出现负数时终止程序    
4、执行账户金额操作时假如出现错误的话，会再执行一次，2次都失败的话就进行回滚并退出程序    
