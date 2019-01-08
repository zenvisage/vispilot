import math
import matplotlib.pyplot as plt
import numpy as np
import StringIO
import seaborn as sns
sns.set_context(
   "talk",
   font_scale=1,
   rc={
       "lines.linewidth": 2,
       #"text.usetex": True,
       "font.family": 'serif',
       "font.serif": ['Palatino'],
       "axes.titlesize":30,
       "axes.labelsize":30,
       "xtick.labelsize": 23,
       "ytick.labelsize": 23
   })
sns.set_style('white')

from matplotlib import rc
font = {'family': 'serif', 'serif': ['Palatino'], 'size': 14}
rc('font', **font)

plt.rcParams["figure.figsize"] = (5,5)
plt.rcParams["figure.max_open_warning"]=500

def millify(n):
    n = float(n)
    millidx = max(0, min(len(millnames) - 1,
                         int(math.floor(0 if n == 0 else math.log10(abs(n)) / 3))))
    if millidx < 2:
        return int(n)
    else:
        return '{:.0f}{}'.format(n / 10 ** (3 * millidx), millnames[millidx])


def get_cmap(n, name='hsv'):
    '''Returns a function that maps each index in 0, 1, ..., n-1 to a distinct
    RGB color; the keyword argument name must be a standard mpl colormap name.'''
    return plt.cm.get_cmap(name, n)


def autolabel(rects, ax):
    # Get y-axis height to calculate label position from.
    (y_bottom, y_top) = ax.get_ylim()
    y_height = y_top - y_bottom


    for rect in rects:
        height = rect.get_height()
        label_position = height + (y_height * 0.01)

        ax.text(rect.get_x() + rect.get_width() / 2., label_position - (height / 2),
                '%.1f' % height, fontsize=24,
                ha='center', va='bottom')


def bar_chart(yVals, xAttrs, xtitle="", ytitle="", title="", top_right_text="", N=1, width=0.1):

    ind = np.arange(N)  # the x locations for the groups
    if len(yVals)==2:
        colors = ["#fc9272","#9ecae1"]
        sns.set_palette(sns.color_palette(colors))
    else:
        colors = ["#9ecae1","#99e699","#fc9272"]
        sns.set_palette(sns.color_palette(colors))
    fig, ax = plt.subplots()
    #cmap = get_cmap(len(yVals) + 1)
    rects = []
    for i in range(len(yVals)):
        rect = ax.bar(ind + (i+0.5) * width, yVals[i], width, ecolor="black")
        rects.append(rect)

    xtitle = xtitle.replace('"', '')
    ytitle = ytitle.replace('"', '')
    #ax.set_xlabel(xtitle, fontsize=9)
    #ax.set_ylabel(ytitle, fontsize=9)
    #ax.set_xlabel(xtitle)
    #ax.set_ylabel(ytitle)
    title = title.replace('"','')
    title = title.replace("#", ",\n")
    if title == "collapsed":
        title = " "
    elif title[0] == ',':
        title = title[2:-2]
    #publication figure settings
    # if title =="overall":
    #     ax.set_title("overall",fontdict={'fontsize': 28,
    #                              'fontweight' : 15})
    # elif title.count('\n')==0:
    #     ax.set_title("______________",fontdict={'fontsize': 28,
    #                              'fontweight' : 15,'color':'white'})#, loc='left')#.set_position([-0.2,110])
    #     ax.text(-0.15, 105, title,fontsize=30,weight=18)#,loc="center")
    # elif title.count('\n')==1:
    #     ax.set_title("______________\n_____________",fontdict={'fontsize': 28,
    #                              'fontweight' : 15,'color':'white'})#, loc='left')#.set_position([-0.2,110])
    #     ax.text(-0.15, 105, title,fontsize=30,weight=18)#,loc="center")
    # elif title.count('\n')==2:
    #     ax.set_title("______________\n______________\n______________",fontdict={'fontsize': 28,
    #                              'fontweight' : 15,'color':'white'})#, loc='left')#.set_position([-0.2,110])
    #     ax.text(-0.15, 105, title,fontsize=30,weight=18)#,loc="center")
    if title.count(',') < 3:
        ax.set_title(title)
    else:
        ax.set_title(title)
    # In case the font is too large for long titles

    # ax.set_title("test", fontsize=12)

    # Left vertical title
    # title.set_position((1.1,0.9))
    # title.set_rotation(270)

    xmin = -0.05
    xmax = 0.25 + 0.1 * (len(yVals) - 2)
    xtickpos = [np.abs(xmin - xmax) / (len(yVals) + 1.5) * (i + 0.7) for i in range(len(yVals))]
    # print "xtickpos:",xtickpos
    xtickpos = [0.025, 0.15, 0.27]
    ax.set_xticks(xtickpos)
    ax.set_xticklabels(xAttrs)
    # ax.set_xlabel(xtitle,fontsize=12)

    # ax.legend((rects1[0], rects2[0]), xAttrs)
    ax.annotate(top_right_text, xy=(0.75, 0.75), xycoords='axes fraction')
    ax.set_xlim(xmin, xmax)
    ax.set_ylim((0, 110))
    size = 0
    for rect in rects:
        size += 1
    #if size < 3:
    #    font_size = 11
    #else:
    #    font_size = 7
    # In case the font is too large for long titles
    for rect in rects:
        autolabel(rect, ax)


    #font = {'family': 'serif', 'serif': ['Palatino'], 'size': 18}
    #sns.set(palette="dark",color_codes=True)

    sns.despine(top=True, right=True)
    # save as svg string
    imgdata = StringIO.StringIO()
    plt.tight_layout()
    fig.savefig(imgdata, format='svg')
    imgdata.seek(0)  # rewind the data
    svg_str = imgdata.buf  # this is svg data

    return svg_str


if __name__ == "__main__":
    # attributes = ['cap_surface','cap_color','bruises']
    # G = generateLattice('type','*','COUNT',attributes,'mushroom',DEBUG=True)
    data = bar_chart([70, 30], ["M", "F"], "Gender", "COUNT(id)")
    print data
