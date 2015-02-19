本代码主要目的是在mongodb中实现事务操作   
其中Snapshot目录的代码是使用快照（也就是版本控制）的方式实现事务操作，详细说明可以参看我博客上的这边文章：[Go](http://cjyfff.sinaapp.com/blog/25/)   
Two Phase Commits目录中的代码，顾名思义就是使用Two Phase Commits的方法来实现事务操作。   