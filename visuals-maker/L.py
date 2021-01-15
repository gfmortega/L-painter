from paintertool import Painter
from imageio import mimsave, imread
import os, shutil

class Solver:
    def set_dim(self, n, x, y):
        self.n = n
        self.x = x
        self.y = y

    def load_dim(self,settings):
        self.set_dim(*[
                        int(settings.readline()),
                        *map(int,settings.readline().split()),
                    ])

    def add_painter(self,lines):
        k = int(lines[0])
        palette_name = lines[1].rstrip()
        color = {'#': (0,0,0), '.': (255,255,255)}
        for c, col in zip('ABC',lines[2:]):
            if "," in col: #RGB
                color[c] = tuple(map(int,col.split(",")))
            else:
                color[c] = tuple([int(col[i:i+2].upper(),16) for i in range(0,6,2)])

        
        self.painters.append(Painter(k,palette_name,color))

    def load_painter(self,settings):
        self.add_painter([settings.readline() for i in range(5)])

    def load_all(self,settings_file): # read everything from one file
        with open(settings_file,'r') as settings:  
            self.load_dim(settings)
            self.load_painter(settings)

    def clean_frames(self):
        # https://stackoverflow.com/questions/185936/how-to-delete-the-contents-of-a-folder
        folder = './frames'
        for filename in os.listdir(folder):
            file_path = os.path.join(folder, filename)
            try:
                if os.path.isfile(file_path) or os.path.islink(file_path):
                    os.unlink(file_path)
                elif os.path.isdir(file_path):
                    shutil.rmtree(file_path)
            except Exception as e:
                print('Failed to delete %s. Reason: %s' % (file_path, e))

    def __init__(self):
        self.clean_frames()

        self.ctr = 0 # of frames, for gif
        self.grid = None
        self.painters = []

    def recurse(self, grid, x_offset, y_offset, n, bad_x, bad_y, every_frame = False):
        if n==0:
            return
     
        if n==1:
            color = 'A' if ((x_offset>>1)&1)^((y_offset>>1)&1) else 'B'
        else:
            color = 'C'
     
        pend = [] # pend recursive calls so we can place the whole piece first
        for dx in [0,1]:
            for dy in [0,1]:
                x = x_offset+(1<<(n-1))-1+dx
                y = y_offset+(1<<(n-1))-1+dy
                new_x_offset = x_offset+(1<<(n-1))*dx
                new_y_offset = y_offset+(1<<(n-1))*dy
     
                if (x>>(n-1))==(bad_x>>(n-1)) and (y>>(n-1))==(bad_y>>(n-1)):
                    pend.append((grid, new_x_offset, new_y_offset, n-1, bad_x, bad_y, every_frame))
                else :
                    grid[x][y] = color
                    pend.append((grid, new_x_offset, new_y_offset, n-1, x, y, every_frame))

        if every_frame:
            for painter in self.painters:
                painter.save_to_image(grid, painter.current_frame_name())

        for args in pend:
            self.recurse(*args)

    def output_name(self, op, painter, filetype):
        return f"img/{painter.palette_name}-{op}-{self.n}-{self.x}-{self.y}-{painter.k}.{filetype}"

    def gif(self):
        self.ctr = 0
        grid = [['.' for j in range(1<<self.n)] for i in range(1<<self.n)]
        grid[self.x-1][self.y-1] = '#'

        for painter in self.painters:
            painter.save_to_image(grid, painter.current_frame_name())
        self.recurse(grid, 0, 0, self.n, self.x-1, self.y-1, every_frame = True)
        
        for painter in self.painters:
            mimsave(self.output_name("construct",painter,"gif"),[imread(painter.format_frame(i)) for i in range(painter.ctr)])
            print(f'Created {self.output_name("construct",painter,"gif")}')
    def png(self, lazy=False):
        grid = [['.' for j in range(1<<self.n)] for i in range(1<<self.n)]
        grid[self.x-1][self.y-1] = '#'
        self.recurse(grid, 0, 0, self.n, self.x-1, self.y-1, every_frame = False)

        for painter in self.painters:
            painter.save_to_image(grid, self.output_name("final", painter, "png"))
