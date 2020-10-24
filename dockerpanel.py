from os import environ as env
import http.server
from http.server import HTTPServer, CGIHTTPRequestHandler

def dockerpanel():
    port = int(env['PORT']) if 'PORT' in env else 8080
    server_address = ('', port)
    handler = DockerPanelRequestHandler
    httpd = HTTPServer(server_address, handler)
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        httpd.socket.close()


class DockerPanelRequestHandler(CGIHTTPRequestHandler):
    cgi_directories = ['/api']

    def do_GET(self):
        if self.path == '/index.html':
            self.send_response(302)
            self.send_header('Location', '/')
            self.end_headers()
        elif self.path == '/':
            self.send_response(200)
            self.end_headers()
            title = env['TITLE'] if 'TITLE' in env else 'Dockerpanel'
            with open('index.html', 'r') as file:
                content = file.read().replace('{{TITLE}}', title)
            self.wfile.write(content.encode())
        elif self.path == '/theme':
            self.send_response(200)
            self.send_header('Content-Type', 'text/css')
            self.end_headers()
            theme = env['THEME'] if 'THEME' in env else 'default'
            with open(f'themes/{theme}.css', 'rb') as file:
                self.wfile.write(file.read())

        else:
            super().do_GET()

if __name__ == '__main__':
    dockerpanel()
