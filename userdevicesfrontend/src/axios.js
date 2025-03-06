import axios from "axios";

const createAxiosInstance = (name, port) => {
  return axios.create({
    baseURL: `http://${name}:${port}`,
    headers: {
      post: {
        "Content-Type": "application/json",
      },
    },
  });
};

export const axiosInstance8080 = createAxiosInstance("user.localhost", 80);
export const axiosInstance8081 = createAxiosInstance("device.localhost", 80);
export const axiosInstance8087 = createAxiosInstance(
  "consumption.localhost",
  80
);
export const axiosInstance8089 = createAxiosInstance("chat.localhost", 80);
