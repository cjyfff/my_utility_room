"""测试数据库回滚"""

from sqlalchemy import Column, String, create_engine, Integer
from sqlalchemy.orm import sessionmaker
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()
engine = create_engine('mysql://root:xxxxxx@localhost:3306/test')
db_session = sessionmaker(bind=engine)


class Test01(Base):
    __tablename__ = 'test01'

    id = Column(Integer, primary_key=True)
    val = Column(Integer, default=None)
    u_test = Column(String(30), default=None, unique=True)


def session_01():
    session = db_session()
    try:
        new_data = Test01(val=2017)
        session.add(new_data)

        print('哈哈，数据已添加')
        data = session.query(Test01).filter(Test01.val == 2016).one()
        print('id: ', data)
        # a = 1 / 0
        session.commit()
    except Exception as err:
        print(str(err))
        session.rollback()

    session.close()


def main():
    session_01()


if __name__ == '__main__':
    main()
