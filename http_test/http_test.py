import requests
import time


def main():
    url = 'http://127.0.0.1:18083/consumer'
    for i in range(1000000):
        print(requests.get(url).text)
        time.sleep(0.01)


if __name__ == '__main__':
    main()
