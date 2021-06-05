<template>

  <div>
    <div class="navigation">
      <div class="menu">
        <ul>
          <li v-for="(row, index) in types"
              @click="setActive(index)"
              :class="{'active':row.active}">
            <a href="#">
              <font-awesome-icon class="icon" :icon="row.icon"/>
              <span class="title">{{ row.title }}</span>
            </a>
          </li>
          <div :class="{'indicator':true, 'on':(activeIndex>=0)}"
               :style="'transform: translateX(calc('+(70 * activeIndex) + 'px))'"></div>
        </ul>
      </div>
      <div class="section">
        <canvas ref="canvas"></canvas>
        <div class="tool" @click="togglePlay()">
          <font-awesome-icon v-if="!play" class="icon" :icon="['fa', 'play']"/>
          <font-awesome-icon v-if="play" class="icon" :icon="['fa', 'stop']"/>
        </div>
      </div>
    </div>
    <span style="color: white;">
      {{ connected ? 'Connected' : 'Disconnected' }}
      <div style="font-size: small">
        <div v-for="row in types">{{ row.title }}: {{ row.itr }}</div>
      </div>
    </span>
  </div>
</template>

<script>
export default {
  data() {
    return {
      data: {
        list: []
      },
      play: false,
      stop: false,
      connected: false,
      activeIndex: -1,
      types: [
        {title: "Bubble Sort", icon: ['fas', 'ellipsis-h'], sortType: "BUBBLE_SORT", active: false, itr: 0},
        {title: "Quick Sort", icon: ['fas', 'fighter-jet'], sortType: "QUICK_SORT", active: false, itr: 0},
        {title: "Insertion Sort", icon: ['fas', 'paste'], sortType: "INSERTION_SORT", active: false, itr: 0},
        {title: "Heap Sort", icon: ['fas', 'cubes'], sortType: "HEAP_SORT", active: false, itr: 0},
        {title: "Selection Sort", icon: ['fas', 'check'], sortType: "SELECTION_SORT", active: false, itr: 0}
      ]
    }
  },
  mounted() {
    setInterval(() => {
      if (!this.connected) {
        this.connectToSocket()
      }
    }, 1000)
    this.defineCanvas()
  },
  methods: {
    connectToSocket() {
      this.socket = new WebSocket("ws://localhost:8080/websocket")
      this.socket.onopen = () => {
        this.connected = true
      }
      this.socket.onclose = () => {
        this.connected = false
      }
      this.socket.onmessage = (msg) => {
        let obj = JSON.parse(msg.data)
        if (obj) {
          this.updateData(obj)
        }
      }
    },
    togglePlay() {
      this.play = !this.play
      this.stop = !this.play
      this.clearCanvas()
      this.pushSocket()
    },
    clearCanvas() {
      if (this.canvas) {
        this.context.clearRect(0, 0, this.canvas.width, this.canvas.height)
      }
    },
    defineCanvas() {
      this.canvas = this.$refs.canvas
      this.canvas.width = 450
      this.canvas.height = 500
      this.context = this.canvas.getContext("2d")
    },
    updateData(obj) {
      this.data = obj
      this.types.forEach((row, i) => {
        if(this.activeIndex === i){
          row.itr++
        }
      })
      this.draw()
    },
    setActive(ind) {
      this.types.forEach((row, i) => {
        row.active = (ind === i)
        if(row.active){
          row.itr = 0
        }
      })
      this.activeIndex = ind
      this.play = false
      this.stop = true
      this.clearCanvas()
      this.pushSocket()
    },
    pushSocket() {
      if (this.activeIndex >= 0) {
        let obj = this.types[this.activeIndex]
        obj.play = this.play
        obj.stop = this.stop
        this.socket.send(JSON.stringify(obj))
      }
    },
    draw() {
      let xStep = 0;
      let yStep = 0;
      if (this.data && this.activeIndex >= 0) {
        this.clearCanvas();
        this.data.list.forEach((line, ind) => {
          if (yStep === 0) {
            yStep = this.canvas.height - this.canvas.height / 800
            xStep = this.canvas.width / this.data.list.length
          }
          let x = (xStep * ind)
          let y = line.val
          this.drawLine(x, y, xStep - 1, yStep, this.data.index.indexOf(ind) >= 0 ? 'yellow' : 'grey')
        })
      }
    },
    drawLine(x, y, w, h, fill) {
      this.context.beginPath()
      this.context.rect(x, y, w, h)
      this.context.fillStyle = fill
      this.context.fill()
      this.context.lineWidth = 0
      this.context.strokeStyle = 'black'
    }
  }
  ,
  components: {}
}
</script>
