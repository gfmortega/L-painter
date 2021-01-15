from L import Solver
solver = Solver()

with open("settings.txt",'r') as settings:
	solver.load_dim(settings)
	k = int(settings.readline())
	# no longer read the colors

with open("palettes.txt") as sample:
	try:
		while True:
			solver.add_painter([k,*[sample.readline() for i in range(4)]])
			sample.readline() # eat the space between lines
	except:
		pass

solver.png()
