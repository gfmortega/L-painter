from PIL import Image
class Painter:
	def __init__(self, k, palette_name, color):
		self.k = k
		self.palette_name = palette_name
		self.color = color

		self.ctr = 0 # for frames

	def format_frame(self,n):
		return f"frames/{self.palette_name}-{self.k}-{n}.png"

	def current_frame_name(self):
		self.ctr += 1
		return self.format_frame(self.ctr-1)

	def save_to_image(self, grid, filename):
		k = self.k
		color = self.color

		n = len(grid) # assume grid is square, too lazy to generalize
		wall = k//2

		with Image.new('RGB',(k*n+2*wall,k*n+2*wall)) as painting:
			for i in range(k*n+2*wall):
				for j in range(wall):
					painting.putpixel((i,j),(0,0,0))
					painting.putpixel((i,k*n+2*wall-j-1),(0,0,0))
					painting.putpixel((j,i),(0,0,0))
					painting.putpixel((k*n+2*wall-j-1,i),(0,0,0))

			for i in range(k*n):
				for j in range(k*n):
					painting.putpixel((i+wall,j+wall),color[grid[i//k][j//k]])

			painting.save(filename,"PNG")
			print(f"Created {filename}")