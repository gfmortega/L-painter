from PIL import Image
class Painter:
	def __init__(self, color, k):
		this.color = color
		this.k = k

	def save_to_image(grid, filename):
		n = len(grid)
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