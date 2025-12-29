export default function Button({ variant = "primary", children, ...props }) {
  const className = ["button", variant === "secondary" ? "secondary" : "", variant === "outline" ? "outline" : ""]
    .filter(Boolean)
    .join(" ");

  return (
    <button className={className} type="button" {...props}>
      {children}
    </button>
  );
}
